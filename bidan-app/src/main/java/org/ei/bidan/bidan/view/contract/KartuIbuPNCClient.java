package org.ei.bidan.bidan.view.contract;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.ei.bidan.bidan.domain.Anak;
import org.ei.bidan.domain.ANCServiceType;
import org.ei.bidan.util.DateUtil;
import org.ei.bidan.view.contract.AlertDTO;
import org.ei.bidan.view.contract.ServiceProvidedDTO;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.contract.Visits;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.ei.bidan.util.StringUtil.humanize;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KartuIbuPNCClient extends BidanSmartRegisterClient implements KartuIbuANCSmartRegisterClient {

    public static final String CATEGORY_PNC = "pnc";
    public static final String CATEGORY_TT = "tt";
    public static final String CATEGORY_HB = "hb";

    private static final String[] SERVICE_CATEGORIES = {CATEGORY_PNC, CATEGORY_TT,
            CATEGORY_HB};

    private String entityId;
    private String kiNumber;
    private String puskesmas;
    private String name;
    private String pncNumber;
    private String dateOfBirth;
    private String husbandName;
    private String photo_path;
    private boolean isHighPriority;
    private boolean isHighRisk;
    private String riskFactors;
    private String locationStatus;
    private String edd;
    private String village;
    private String plan;
    private String komplikasi;
    private String otherKomplikasi;
    private String metodeKontrasepsi;
    private String tdSistolik;
    private String tdDiastolik;
    private String tdSuhu;
    private String tempatPersalinan;
    private String tipePersalinan;
    private String motherCondition;

    private List<AnakClient> children;
    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;
    private String entityIdToSavePhoto;
    private Map<String, Visits> serviceToVisitsMap;

    public KartuIbuPNCClient(String entityId, String village, String puskesmas, String name, String dateOfBirth) {
        this.entityId = entityId;
        this.puskesmas = puskesmas;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.village = village;
        this.serviceToVisitsMap = new HashMap<String, Visits>();
    }

    @Override
    public String eddForDisplay() {
        return null;
    }

    @Override
    public LocalDateTime edd() {
        return parse(edd);
    }

    @Override
    public String pastDueInDays() {
        return null;
    }

    @Override
    public AlertDTO getAlert(ANCServiceType type) {
        return null;
    }

    @Override
    public boolean isVisitsDone() {
        return false;
    }

    @Override
    public boolean isTTDone() {
        return false;
    }

    @Override
    public String visitDoneDateWithVisitName() {
        return null;
    }

    @Override
    public String ttDoneDate() {
        return null;
    }

    @Override
    public String ifaDoneDate() {
        return null;
    }

    @Override
    public String ancNumber() {
        return null;
    }

    @Override
    public String riskFactors() {
        return null;
    }

    @Override
    public ServiceProvidedDTO serviceProvidedToACategory(String category) {
        return null;
    }

    @Override
    public String getHyperTension(ServiceProvidedDTO ancServiceProvided) {
        return null;
    }

    @Override
    public ServiceProvidedDTO getServiceProvidedDTO(String serviceName) {
        return null;
    }

    @Override
    public List<ServiceProvidedDTO> allServicesProvidedForAServiceType(String serviceType) {
        return null;
    }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return Strings.isNullOrEmpty(name) ? "" : name;
    }

    @Override
    public String displayName() {
        return humanize(name());
    }

    @Override
    public String village() {
        return humanize(village);
    }
    @Override
    public String wifeName() {
        return humanize(name());
    }

    @Override
    public String husbandName() {
        return Strings.isNullOrEmpty(husbandName) ? "" : husbandName;
    }

    @Override
    public int age() {
        return StringUtils.isBlank(dateOfBirth) ? 0 : Years.yearsBetween(LocalDate.parse(dateOfBirth), LocalDate.now()).getYears();
    }

    @Override
    public int ageInDays() {
        return StringUtils.isBlank(dateOfBirth) ? 0 : Days.daysBetween(LocalDate.parse(dateOfBirth), DateUtil.today()).getDays();
    }


    @Override
    public String ageInString() {
        return "(" + age() + ")";
    }

    @Override
    public boolean isSC() {
        return false;
    }

    @Override
    public boolean isST() {
        return false;
    }

    @Override
    public boolean isHighRisk() {
        return false;
    }

    @Override
    public boolean isHighPriority() {
        return false;
    }

    @Override
    public boolean isBPL() {
        return false;
    }

    @Override
    public String profilePhotoPath() {
        return null;
    }

    @Override
    public String locationStatus() {
        return null;
    }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return name.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || husbandName.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || String.valueOf(kiNumber).startsWith(filterCriterion)
                || String.valueOf(puskesmas).startsWith(filterCriterion);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return 0;
    }

    public String getPuskesmas() {
        return puskesmas;
    }

    public String kiNumber() {
        return kiNumber;
    }

    public void setTempatPersalinan(String tempatPersalinan) {
        this.tempatPersalinan = tempatPersalinan;
    }

    public String tempatPersalinan() {
        return Strings.isNullOrEmpty(tempatPersalinan) ? "-" :humanize(tempatPersalinan);
    }

    public void setTipePersalinan(String tipePersalinan) {
        this.tipePersalinan = tipePersalinan;
    }

    public String tipePersalinan() {
        return Strings.isNullOrEmpty(tipePersalinan) ? "-" : humanize(tipePersalinan);
    }

    public String plan() { return humanize(plan); }

    public KartuIbuPNCClient withHusband(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public KartuIbuPNCClient withKINumber(String kiNumber) {
        this.kiNumber = kiNumber;
        return this;
    }

    public KartuIbuPNCClient withEDD(String edd) {
        this.edd = edd;
        return this;
    }

    public KartuIbuPNCClient withPlan(String plan) {
        this.plan = plan;
        return this;
    }

    public KartuIbuPNCClient withKomplikasi(String komplikasi) {
        this.komplikasi = komplikasi;
        return this;
    }

    public KartuIbuPNCClient withOtherKomplikasi(String otherKomplikasi) {
        this.otherKomplikasi = otherKomplikasi;
        return this;
    }

    public KartuIbuPNCClient withMetodeKontrasepsi(String metodeKontrasepsi) {
        this.metodeKontrasepsi = metodeKontrasepsi;
        return this;
    }

    public KartuIbuPNCClient withTandaVital(String tdDiastolik, String tdSistolik, String tdSuhu) {
        this.tdDiastolik = tdDiastolik;
        this.tdSistolik = tdSistolik;
        this.tdSuhu = tdSuhu;
        return this;
    }

    public String tdDiastolik() {
        return Strings.isNullOrEmpty(tdDiastolik) ? "-" : tdDiastolik;
    }

    public String tdSistolik() {
        return Strings.isNullOrEmpty(tdSistolik) ? "-" : tdSistolik;
    }

    public String komplikasi() {
        return Strings.isNullOrEmpty(komplikasi) ? "-" : humanize(komplikasi);
    }

    public String otherKomplikasi() {
        return Strings.isNullOrEmpty(otherKomplikasi) ? "" : humanize(otherKomplikasi);
    }

    public String tdSuhu() {
        return Strings.isNullOrEmpty(tdSuhu) ? "-" : tdSuhu;
    }

    public String metodeKontrasepsi() {
        return humanize(metodeKontrasepsi);
    }

    public KartuIbuPNCClient withChildren(List<AnakClient> children) {
        this.children = children;
        return this;
    }

    public List<AnakClient> children() {
        return children;
    }

    public AnakClient getLastChild() { return children.get(children.size()-1); }

    public String getLastBirth() {
        if(children==null) return "";

        return getLastChild().dateOfBirth();
    }

    public void setMotherCondition(String motherCondition) {
        this.motherCondition = motherCondition;
    }

    public String motherCondition() {return humanize(this.motherCondition);}
}
