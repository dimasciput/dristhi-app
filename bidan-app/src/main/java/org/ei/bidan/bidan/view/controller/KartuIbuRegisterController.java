package org.ei.bidan.bidan.view.controller;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import org.ei.bidan.AllConstants;
import org.ei.bidan.bidan.domain.Anak;
import org.ei.bidan.bidan.domain.Ibu;
import org.ei.bidan.bidan.domain.KartuIbu;
import org.ei.bidan.bidan.repository.AllKohort;
import org.ei.bidan.bidan.view.contract.KIChildClient;
import org.ei.bidan.bidan.view.contract.KartuIbuClient;
import org.ei.bidan.bidan.view.contract.KartuIbuClients;
import org.ei.bidan.bidan.repository.AllKartuIbus;
import org.ei.bidan.util.Cache;
import org.ei.bidan.util.CacheableData;
import org.ei.bidan.util.EasyMap;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.List;
import static java.util.Collections.sort;
import static org.ei.bidan.AllConstants.KartuIbuFields.*;
import static org.ei.bidan.AllConstants.KeluargaBerencanaFields.*;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class KartuIbuRegisterController  extends CommonController{
    private static final String KI_CLIENTS_LIST = "KIClientsList";
    public static final String STATUS_DATE_FIELD = "date";
    public static final String ANC_STATUS = "anc";
    public static final String PNC_STATUS = "pnc";
    public static final String STATUS_TYPE_FIELD = "type";

    private final AllKartuIbus allKartuIbus;
    private final Cache<String> cache;
    private final Cache<KartuIbuClients> kartuIbuClientsCache;
    private final AllKohort allKohort;

    public KartuIbuRegisterController(AllKartuIbus allKartuIbus, Cache<String> cache,
                                      Cache<KartuIbuClients> kartuIbuClientsCache,
                                      AllKohort allKohort) {
        this.allKartuIbus = allKartuIbus;
        this.cache = cache;
        this.kartuIbuClientsCache = kartuIbuClientsCache;
        this.allKohort = allKohort;
    }

    public KartuIbuClients getKartuIbuClients() {
        return kartuIbuClientsCache.get(KI_CLIENTS_LIST, new CacheableData<KartuIbuClients>() {
            @Override
            public KartuIbuClients fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.all();
                KartuIbuClients kartuIbuClients = new KartuIbuClients();

                for (KartuIbu kartuIbu : kartuIbus) {

                    KartuIbuClient kartuIbuClient = new KartuIbuClient(kartuIbu.getCaseId(),
                            kartuIbu.getDetail(PUSKESMAS_NAME), kartuIbu.getDetail(PROPINSI),
                            kartuIbu.getDetail(KABUPATEN), kartuIbu.getDetail(POSYANDU_NAME),
                            kartuIbu.getDetail(MOTHER_ADDRESS), kartuIbu.getDetail(MOTHER_NUMBER),
                            kartuIbu.getDetail(MOTHER_NAME), kartuIbu.getDetail(MOTHER_AGE),
                            kartuIbu.getDetail(MOTHER_BLOOD_TYPE),
                            kartuIbu.getDetail(HUSBAND_NAME),
                            kartuIbu.dusun())
                            .withDateOfBirth(kartuIbu.getDetail(MOTHER_DOB))
                            .withParity(kartuIbu.getDetail(NUMBER_PARTUS))
                            .withNumberOfAbortions(kartuIbu.getDetail(NUMBER_ABORTIONS))
                            .withNumberOfPregnancies(kartuIbu.getDetail(NUMBER_OF_PREGNANCIES))
                            .withNumberOfLivingChildren(kartuIbu.getDetail(NUMBER_OF_LIVING_CHILDREN));
                    kartuIbuClient.setIsHighPriority(kartuIbu.getDetail(IS_HIGH_PRIORITY));
                    kartuIbuClient.setIsHighRisk(kartuIbu.getDetail(IS_HIGH_RISK));
                    kartuIbuClient.setEdd(kartuIbu.getDetail(EDD));
                    kartuIbuClient.setIsHighRiskPregnancy(kartuIbu.getDetail(IS_HIGH_RISK_PREGNANCY));
                    kartuIbuClient.setHighRiskLabour(kartuIbu.getDetail(IS_HIGH_RISK_LABOUR));
                    updateStatusInformation(kartuIbu, kartuIbuClient);
                    updateChildrenInformation(kartuIbuClient);
                    kartuIbuClients.add(kartuIbuClient);
                }
                sortByName(kartuIbuClients);
                return kartuIbuClients;
            }
        });
    }

    private void sortByName(List<?extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.wifeName().compareToIgnoreCase(o2.wifeName());
            }
        });
    }

    private void updateChildrenInformation(KartuIbuClient kartuIbuClient) {
        List<Anak> children = allKohort.findAllAnakByKIId(kartuIbuClient.entityId());
        sortByDateOfBirth(children);
        Iterable<Anak> youngestTwoChildren = Iterables.skip(children, children.size() < 2 ? 0 : children.size() - 2);
        for (Anak child : youngestTwoChildren) {
            kartuIbuClient.addChild(new KIChildClient(child.getCaseId(), child.getGender(), child.getDateOfBirth()));
        }
    }

    private void sortByDateOfBirth(List<Anak> children) {
        sort(children, new Comparator<Anak>() {
            @Override
            public int compare(Anak child, Anak anotherChild) {
                return LocalDate.parse(child.getDateOfBirth()).compareTo(LocalDate.parse(anotherChild.getDateOfBirth()));
            }
        });
    }

    //#TODO: Needs refactoring
    private void updateStatusInformation(KartuIbu kartuIbu, KartuIbuClient kartuIbuClient) {
        Ibu ibu = allKohort.findIbuWithOpenStatusByKIId(kartuIbu.getCaseId());

        if(ibu!=null) {
            kartuIbuClient.setIsHighRiskFromANC(!Strings.isNullOrEmpty(ibu.getDetail(CHRONIC_DISEASE)));
        }

        if( ibu == null && kartuIbu.hasKBMethod()) {
            kartuIbuClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, KELUARGA_BERENCANA)
                            .put(STATUS_DATE_FIELD, kartuIbu.getDetail(VISITS_DATE)).map());

            kartuIbuClient.setKbMethod(kartuIbu.getDetail(CONTRACEPTION_METHOD));
            kartuIbuClient.setKbStart(kartuIbu.getDetail(VISITS_DATE));
            return;
        }

        if (ibu != null && ibu.isANC()) {
            kartuIbuClient
                    .withStatus(EasyMap.create(STATUS_TYPE_FIELD, ANC_STATUS)
                            .put(STATUS_DATE_FIELD, ibu.getReferenceDate()).map());
            kartuIbuClient.setKbMethod("-");
            return;
        }

        if (ibu != null && ibu.isPNC()) {
            kartuIbuClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, PNC_STATUS)
                    .put(STATUS_DATE_FIELD, ibu.getReferenceDate()).map());
            kartuIbuClient.setKbMethod("-");
            return;
        }
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        return onRandomNameChars(
                client,
                getKartuIbuClients(),
                allKartuIbus.randomDummyName(),
                AllConstants.DIALOG_DOUBLE_SELECTION_NUM);
    }

}
