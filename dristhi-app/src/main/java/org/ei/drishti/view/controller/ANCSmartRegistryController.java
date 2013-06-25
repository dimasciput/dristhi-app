package org.ei.drishti.view.controller;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.ei.drishti.domain.Alert;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.ServiceProvided;
import org.ei.drishti.repository.AllBeneficiaries;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.AlertService;
import org.ei.drishti.service.ServiceProvidedService;
import org.ei.drishti.util.Cache;
import org.ei.drishti.util.CacheableData;
import org.ei.drishti.view.contract.ANCClient;
import org.ei.drishti.view.contract.AlertDTO;
import org.ei.drishti.view.contract.ServiceProvidedDTO;
import org.ei.drishti.view.contract.Village;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;
import static org.ei.drishti.domain.ServiceProvided.*;

public class ANCSmartRegistryController {
    private static final String ANC_1_ALERT_NAME = "ANC 1";
    private static final String ANC_2_ALERT_NAME = "ANC 2";
    private static final String ANC_3_ALERT_NAME = "ANC 3";
    private static final String ANC_4_ALERT_NAME = "ANC 4";
    private static final String IFA_1_ALERT_NAME = "IFA 1";
    private static final String IFA_2_ALERT_NAME = "IFA 2";
    private static final String IFA_3_ALERT_NAME = "IFA 3";
    private static final String LAB_REMINDER_ALERT_NAME = "REMINDER";
    private static final String TT_1_ALERT_NAME = "TT 1";
    private static final String TT_2_ALERT_NAME = "TT 2";
    private static final String HB_TEST_1_ALERT_NAME = "Hb Test 1";
    private static final String HB_TEST_2_ALERT_NAME = "Hb Test 2";
    private static final String HB_FOLLOWUP_TEST_ALERT_NAME = "Hb Followup Test";

    private static final String ANC_CLIENTS_LIST = "ANCClientList";
    private AllEligibleCouples allEligibleCouples;
    private AllBeneficiaries allBeneficiaries;
    private AlertService alertService;
    private Cache<String> cache;
    private final ServiceProvidedService serviceProvidedService;

    public ANCSmartRegistryController(ServiceProvidedService serviceProvidedService, AlertService alertService,
                                      AllEligibleCouples allEligibleCouples, AllBeneficiaries allBeneficiaries,
                                      Cache<String> cache) {
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.alertService = alertService;
        this.serviceProvidedService = serviceProvidedService;
        this.cache = cache;
    }

    public String get() {
        return cache.get(ANC_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<ANCClient> ancClients = new ArrayList<ANCClient>();
                List<Pair<Mother, EligibleCouple>> ancsWithEcs = allBeneficiaries.allANCsWithEC();

                for (Pair<Mother, EligibleCouple> ancWithEc : ancsWithEcs) {
                    Mother anc = ancWithEc.getLeft();
                    EligibleCouple ec = ancWithEc.getRight();
                    String photoPath = isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH : ec.photoPath();

                    List<ServiceProvidedDTO> servicesProvided = getServicesProvided(anc.caseId());
                    List<AlertDTO> alerts = getAlerts(anc.caseId());
                    ancClients.add(new ANCClient(anc.caseId(), ec.village(), ec.wifeName(), anc.thaayiCardNumber(), anc.getDetail("edd"), anc.referenceDate())
                            .withHusbandName(ec.husbandName())
                            .withAge(ec.age())
                            .withECNumber(ec.ecNumber())
                            .withANCNumber(anc.getDetail("ancNumber"))
                            .withIsHighPriority(ec.isHighPriority())
                            .withIsHighRisk(anc.isHighRisk())
                            .withIsOutOfArea(ec.isOutOfArea())
                            .withHighRiskReason(anc.getDetail("highRiskReason"))
                            .withCaste(ec.getDetail("caste"))
                            .withEconomicStatus(ec.getDetail("economicStatus"))
                            .withPhotoPath(photoPath)
                            .withAlerts(alerts)
                            .withServicesProvided(servicesProvided)
                    );
                }
                sortByName(ancClients);
                return new Gson().toJson(ancClients);
            }
        });
    }

    public String villages() {
        List<Village> villagesList = new ArrayList<Village>();
        List<String> villages = allEligibleCouples.villages();
        for (String village : villages) {
            villagesList.add(new Village(village));
        }
        return new Gson().toJson(villagesList);
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService.findByEntityIdAndServiceNames(entityId,
                IFA_SERVICE_PROVIDED_NAME,
                TT_1_SERVICE_PROVIDED_NAME,
                TT_2_SERVICE_PROVIDED_NAME,
                TT_BOOSTER_SERVICE_PROVIDED_NAME,
                HB_TEST_SERVICE_PROVIDED_NAME,
                ANC_1_SERVICE_PROVIDED_NAME,
                ANC_2_SERVICE_PROVIDED_NAME,
                ANC_3_SERVICE_PROVIDED_NAME,
                ANC_4_SERVICE_PROVIDED_NAME);
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs.add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(), serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId,
                ANC_1_ALERT_NAME,
                ANC_2_ALERT_NAME,
                ANC_3_ALERT_NAME,
                ANC_4_ALERT_NAME,
                IFA_1_ALERT_NAME,
                IFA_2_ALERT_NAME,
                IFA_3_ALERT_NAME,
                LAB_REMINDER_ALERT_NAME,
                TT_1_ALERT_NAME,
                TT_2_ALERT_NAME,
                HB_TEST_1_ALERT_NAME,
                HB_FOLLOWUP_TEST_ALERT_NAME,
                HB_TEST_2_ALERT_NAME
        );
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    private void sortByName(List<ANCClient> ancClients) {
        sort(ancClients, new Comparator<ANCClient>() {
            @Override
            public int compare(ANCClient oneANCClient, ANCClient anotherANCClient) {
                return oneANCClient.wifeName().compareToIgnoreCase(anotherANCClient.wifeName());
            }
        });
    }
}
