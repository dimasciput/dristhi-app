package org.ei.bidan.bidan.view.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.bidan.AllConstants;
import org.ei.bidan.bidan.domain.Anak;
import org.ei.bidan.bidan.domain.Ibu;
import org.ei.bidan.bidan.domain.KartuIbu;
import org.ei.bidan.bidan.repository.AllKohort;
import org.ei.bidan.bidan.view.contract.AnakClient;
import org.ei.bidan.bidan.view.contract.KartuIbuPNCClient;
import org.ei.bidan.bidan.view.contract.KartuIbuPNCClients;
import org.ei.bidan.util.Cache;
import org.ei.bidan.util.CacheableData;
import org.ei.bidan.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;
import static org.ei.bidan.AllConstants.KartuAnakFields.BIRTH_CONDITION;
import static org.ei.bidan.AllConstants.KartuAnakFields.BIRTH_WEIGHT;
import static org.ei.bidan.AllConstants.KartuPNCFields.*;
import static org.ei.bidan.AllConstants.KartuIbuFields.*;
import static org.ei.bidan.AllConstants.KeluargaBerencanaFields.*;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KartuIbuPNCRegisterController extends CommonController {

    private static final String KI_PNC_CLIENTS_LIST = "KIPNClientsList";

    private final AllKohort allKohort;
    private final Cache<String> cache;
    private final Cache<KartuIbuPNCClients> kartuIbuPNCClientsCache;

    public KartuIbuPNCRegisterController(AllKohort allKohort, Cache<String> cache, Cache<KartuIbuPNCClients> kartuIbuPNCClientsCache) {
        this.allKohort = allKohort;
        this.cache = cache;
        this.kartuIbuPNCClientsCache = kartuIbuPNCClientsCache;
    }

    public KartuIbuPNCClients getKartuIbuPNCClients() {
        return kartuIbuPNCClientsCache.get(KI_PNC_CLIENTS_LIST, new CacheableData<KartuIbuPNCClients>() {
            @Override
            public KartuIbuPNCClients fetch() {
                KartuIbuPNCClients pncClients = new KartuIbuPNCClients();
                List<Pair<Ibu, KartuIbu>> pncsWithKis = allKohort.allPNCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> pncsWithKi : pncsWithKis) {
                    Ibu pnc = pncsWithKi.getLeft();
                    KartuIbu ki = pncsWithKi.getRight();

                    KartuIbuPNCClient kartuIbuClient = new KartuIbuPNCClient(
                            pnc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB))
                            .withHusband(ki.getDetail(HUSBAND_NAME))
                            .withKINumber(ki.getDetail(MOTHER_NUMBER))
                            .withEDD(pnc.getDetail(EDD))
                            .withPlan(pnc.getDetail(PLANNING))
                            .withKomplikasi(pnc.getDetail(COMPLICATION))
                            .withOtherKomplikasi(pnc.getDetail(OTHER_COMPLICATION))
                            .withMetodeKontrasepsi(pnc.getDetail(CONTRACEPTION_METHOD))
                            .withChildren(findChildren(pnc))
                            .withTandaVital(pnc.getDetail(VITAL_SIGNS_TD_DIASTOLIC),
                                    pnc.getDetail(VITAL_SIGNS_TD_SISTOLIC),
                                    pnc.getDetail(VITAL_SIGNS_TEMP));

                    kartuIbuClient.setTempatPersalinan(pnc.getDetail("tempatBersalin"));
                    kartuIbuClient.setTipePersalinan(pnc.getDetail("caraPersalinanIbu"));
                    kartuIbuClient.setMotherCondition(pnc.getDetail(MOTHER_CONDITION));
                    pncClients.add(kartuIbuClient);
                }
                sortByName(pncClients);
                return pncClients;
            }
        });
    }

    private void sortByName(List<?extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        return onRandomNameChars(
                client,
                getKartuIbuPNCClients(),
                allKohort.randomDummyPNCName(),
                AllConstants.DIALOG_DOUBLE_SELECTION_NUM);
    }

    private List<AnakClient> findChildren(Ibu mother) {
        List<Anak> children = allKohort.findAllAnakByIbuId(mother.getId());
        List<AnakClient> childClientList = new ArrayList<AnakClient>();
        for (Anak child : children) {
            childClientList.add(new AnakClient(child.getCaseId(), child.getGender(),
                    child.getDetail(BIRTH_WEIGHT), child.getDetails()).withDOB(child.getDateOfBirth()).withBirthCondition(child.getDetail(BIRTH_CONDITION)));
        }
        return childClientList;
    }
}
