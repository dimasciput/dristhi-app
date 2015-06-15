package org.ei.bidan.bidan.view.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import org.ei.bidan.AllConstants;
import org.ei.bidan.bidan.domain.Anak;
import org.ei.bidan.bidan.domain.KartuIbu;
import org.ei.bidan.bidan.repository.AllKartuIbus;
import org.ei.bidan.bidan.repository.AllKohort;
import org.ei.bidan.bidan.view.contract.KBClient;
import org.ei.bidan.bidan.view.contract.KBClients;
import org.ei.bidan.util.Cache;
import org.ei.bidan.util.CacheableData;
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
public class KohortKBRegisterController extends CommonController{
    private static final String KB_CLIENTS_LIST = "KBClientsList";

    private final AllKartuIbus allKartuIbus;
    private final Cache<String> cache;
    private final Cache<KBClients> kbClientsCache;
    private final AllKohort allKohort;

    public KohortKBRegisterController(AllKartuIbus allKartuIbus, Cache<String> cache, Cache<KBClients> kbClientsCache, AllKohort allKohort) {
        this.allKartuIbus = allKartuIbus;
        this.cache = cache;
        this.kbClientsCache = kbClientsCache;
        this.allKohort = allKohort;
    }

    public KBClients getKBClients() {
        return kbClientsCache.get(KB_CLIENTS_LIST, new CacheableData<KBClients>() {
            @Override
            public KBClients fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.all();
                KBClients KBs = new KBClients();

                for (KartuIbu kartuIbu : kartuIbus) {

                    if(Strings.isNullOrEmpty(kartuIbu.getDetail(CONTRACEPTION_METHOD))) {
                        continue;
                    }

                    KBClient kbClient = new KBClient(
                            kartuIbu.getCaseId(),
                            kartuIbu.getDetail(MOTHER_NAME),
                            kartuIbu.getDetail(HUSBAND_NAME),
                            kartuIbu.dusun(),
                            kartuIbu.getDetail(MOTHER_NUMBER))
                            .withKBMethod(kartuIbu.getDetail(CONTRACEPTION_METHOD))
                            .withIMS(kartuIbu.getDetails().get(IMS))
                            .withHB(kartuIbu.getDetails().get(HB))
                            .withLila(kartuIbu.getDetails().get(LILA))
                            .withPenyakitKronis(kartuIbu.getDetail(ALKI_CHRONIC_DISEASE))
                            .withGravida(kartuIbu.getDetail(NUMBER_OF_PREGNANCIES))
                            .withParity(kartuIbu.getDetail(NUMBER_PARTUS))
                            .withAbortus(kartuIbu.getDetail(NUMBER_ABORTIONS))
                            .withLiveChild(kartuIbu.getDetail(NUMBER_OF_LIVING_CHILDREN))
                            .withTanggalKunjungan(kartuIbu.getDetail(VISITS_DATE))
                            .withKeterangan1(kartuIbu.getDetail(KB_INFORMATION_1))
                            .withKeterangan2(kartuIbu.getDetail(KB_INFORMATION_2))
                            .withDateOfBirth(kartuIbu.getDetail(MOTHER_DOB));

                    KBs.add(kbClient);
                }
                sortByName(KBs);
                return KBs;
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

    private void sortByDateOfBirth(List<Anak> children) {
        sort(children, new Comparator<Anak>() {
            @Override
            public int compare(Anak child, Anak anotherChild) {
                return LocalDate.parse(child.getDateOfBirth()).compareTo(LocalDate.parse(anotherChild.getDateOfBirth()));
            }
        });
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        return onRandomNameChars(
                client,
                getKBClients(),
                allKartuIbus.randomDummyName(),
                AllConstants.DIALOG_DOUBLE_SELECTION_NUM);
    }


}
