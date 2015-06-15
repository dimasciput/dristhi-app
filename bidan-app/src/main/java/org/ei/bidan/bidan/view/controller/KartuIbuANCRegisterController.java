package org.ei.bidan.bidan.view.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.bidan.AllConstants;
import org.ei.bidan.bidan.domain.Ibu;
import org.ei.bidan.bidan.domain.KartuIbu;
import org.ei.bidan.bidan.repository.AllKohort;
import org.ei.bidan.bidan.view.contract.KartuIbuANCClient;
import org.ei.bidan.bidan.view.contract.KartuIbuANCClients;
import org.ei.bidan.util.Cache;
import org.ei.bidan.util.CacheableData;
import org.ei.bidan.view.contract.SmartRegisterClient;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;
import static org.ei.bidan.AllConstants.KartuIbuFields.*;
import static org.ei.bidan.AllConstants.KartuANCFields.*;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KartuIbuANCRegisterController extends CommonController{

    private static final String KI_ANC_CLIENTS_LIST = "KIANCClientsList";

    private final AllKohort allKohort;
    private final Cache<String> cache;
    private final Cache<KartuIbuANCClients> kartuIbuANCClientsCache;

    public KartuIbuANCRegisterController(AllKohort allKohort, Cache<String> cache, Cache<KartuIbuANCClients> kartuIbuANCClientsCache) {
        this.allKohort = allKohort;
        this.cache = cache;
        this.kartuIbuANCClientsCache = kartuIbuANCClientsCache;
    }

    public KartuIbuANCClients getKartuIbuANCClients() {
        return kartuIbuANCClientsCache.get(KI_ANC_CLIENTS_LIST, new CacheableData<KartuIbuANCClients>() {
            @Override
            public KartuIbuANCClients fetch() {
                KartuIbuANCClients ancClients = new KartuIbuANCClients();
                List<Pair<Ibu, KartuIbu>> ancsWithKis = allKohort.allANCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> ancsWithKi : ancsWithKis) {
                    Ibu anc = ancsWithKi.getLeft();
                    KartuIbu ki = ancsWithKi.getRight();

                    KartuIbuANCClient kartuIbuClient = new KartuIbuANCClient(anc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB))
                            .withHusband(ki.getDetail(HUSBAND_NAME))
                            .withKINumber(ki.getDetail(MOTHER_NUMBER))
                            .withEDD(ki.getDetail(EDD))
                            .withANCStatus(anc.getDetail(MOTHER_NUTRITION_STATUS))
                            .withRiskFactors(anc.getDetail(COMPLICATION_HISTORY))
                            .withKunjunganData(anc.getDetail(TRIMESTER))
                            .withTTImunisasiData(anc.getDetail(IMMUNIZATION_TT_STATUS))
                            .withTanggalHPHT(anc.getDetail(HPHT_DATE));

                    kartuIbuClient.setBB(anc.getDetail(WEIGHT_BEFORE));
                    kartuIbuClient.setTB(anc.getDetail(HEIGHT));
                    kartuIbuClient.setLILA(anc.getDetail(LILA_CHECK_RESULT));
                    kartuIbuClient.setBeratBadan(anc.getDetail(WEIGHT_CHECK_RESULT));
                    kartuIbuClient.setPenyakitKronis(anc.getDetail(CHRONIC_DISEASE));
                    kartuIbuClient.setAlergi(anc.getDetail(ALLERGY));
                    kartuIbuClient.setIsHighRisk(ki.getDetail(IS_HIGH_RISK));
                    kartuIbuClient.setIsHighRiskANC(ki.getDetail(IS_HIGH_RISK));
                    kartuIbuClient.setIsHighRiskPregnancy(ki.getDetail(IS_HIGH_RISK_PREGNANCY));
                    kartuIbuClient.setHighRiskLabour(ki.getDetail(IS_HIGH_RISK_LABOUR));
                    kartuIbuClient.setHigRiskPregnancyReason(ki.getDetail("highRiskPregnancyReason"));
                    kartuIbuClient.setRiwayatKomplikasiKebidanan(anc.getDetail(COMPLICATION_HISTORY));
                    ancClients.add(kartuIbuClient);
                }
                sortByName(ancClients);
                return ancClients;
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
                getKartuIbuANCClients(),
                allKohort.randomDummyANCName(),
                AllConstants.DIALOG_DOUBLE_SELECTION_NUM);
    }

}
