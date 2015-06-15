package org.ei.bidan.bidan.view.dialog;

import org.ei.bidan.Context;
import org.ei.bidan.R;
import org.ei.bidan.view.contract.SmartRegisterClients;
import org.ei.bidan.view.dialog.SortOption;

import java.util.Collections;

import static org.ei.bidan.view.contract.SmartRegisterClient.ALL_HIGH_RISK_COMPARATOR;

/**
 * Created by Dimas Ciputra on 6/11/15.
 */
public class AllHighRiskSort implements SortOption {

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_high_risk_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, ALL_HIGH_RISK_COMPARATOR);
        return allClients;
    }
}
