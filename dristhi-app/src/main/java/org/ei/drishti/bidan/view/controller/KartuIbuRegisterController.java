package org.ei.drishti.bidan.view.controller;

import org.ei.drishti.AllConstants;
import org.ei.drishti.bidan.domain.Ibu;
import org.ei.drishti.bidan.domain.KartuIbu;
import org.ei.drishti.bidan.repository.AllIbu;
import org.ei.drishti.bidan.view.contract.KartuIbuClient;
import org.ei.drishti.bidan.view.contract.KartuIbuClients;
import org.ei.drishti.bidan.repository.AllKartuIbus;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.util.Cache;
import org.ei.drishti.util.CacheableData;
import org.ei.drishti.util.EasyMap;
import org.ei.drishti.view.contract.ECClient;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Comparator;
import java.util.List;
import static java.util.Collections.sort;
import static org.ei.drishti.AllConstants.ANCRegistrationFields.EDD;
import static org.ei.drishti.AllConstants.ECRegistrationFields.FAMILY_PLANNING_METHOD_CHANGE_DATE;
import static org.ei.drishti.AllConstants.ECRegistrationFields.REGISTRATION_DATE;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class KartuIbuRegisterController {
    private static final String KI_CLIENTS_LIST = "KIClientsList";
    public static final String STATUS_DATE_FIELD = "date";
    public static final String ANC_STATUS = "anc";
    public static final String STATUS_TYPE_FIELD = "type";
    public static final String STATUS_EDD_FIELD = "edd";

    private final AllKartuIbus allKartuIbus;
    private final Cache<String> cache;
    private final Cache<KartuIbuClients> kartuIbuClientsCache;
    private final AllIbu allIbu;

    public KartuIbuRegisterController(AllKartuIbus allKartuIbus, Cache<String> cache, Cache<KartuIbuClients> kartuIbuClientsCache, AllIbu allIbu) {
        this.allKartuIbus = allKartuIbus;
        this.cache = cache;
        this.kartuIbuClientsCache = kartuIbuClientsCache;
        this.allIbu = allIbu;
    }

    public KartuIbuClients getKartuIbuClients() {
        return kartuIbuClientsCache.get(KI_CLIENTS_LIST, new CacheableData<KartuIbuClients>() {
            @Override
            public KartuIbuClients fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.all();
                KartuIbuClients kartuIbuClients = new KartuIbuClients();

                for (KartuIbu kartuIbu : kartuIbus) {
                    KartuIbuClient kartuIbuClient = new KartuIbuClient(kartuIbu.getCaseId(),
                            kartuIbu.getDetails().get("puskesmas"), kartuIbu.getDetails().get("Propinsi"),
                            kartuIbu.getDetails().get("Kabupaten"), kartuIbu.getDetails().get("Posyandu"),
                            kartuIbu.getDetails().get("Alamatdomisili"), kartuIbu.getDetails().get("NoIbu"),
                            kartuIbu.getDetails().get("Namalengkap"), kartuIbu.getDetails().get("Umur"),
                            kartuIbu.getDetails().get("GolonganDarah"), kartuIbu.getDetails().get("RiwayatKomplikasiKebidanan"),
                            kartuIbu.getDetails().get("Namasuami"), kartuIbu.getDetails().get("TanggalPeriksa"),
                            kartuIbu.getDetails().get("EDD"), kartuIbu.getDetails().get("Desa"))
                            .withDateOfBirth(kartuIbu.getDetails().get("Tanggallahir"));
                    updateStatusInformation(kartuIbu, kartuIbuClient);
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

    //#TODO: Needs refactoring
    private void updateStatusInformation(KartuIbu kartuIbu, KartuIbuClient kartuIbuClient) {
        Ibu ibu = allIbu.findIbuByKartuIbuId(kartuIbu.getCaseId());

        if (ibu != null && ibu.isANC()) {
            kartuIbuClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, ANC_STATUS)
                    .put(STATUS_DATE_FIELD, ibu.getReferenceDate())
                    .put(STATUS_EDD_FIELD, kartuIbu.getDetails().get("EDD")).map());
            return;
        }
    }
}
