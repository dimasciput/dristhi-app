package org.ei.bidan.bidan.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import org.ei.bidan.AllConstants;
import org.ei.bidan.R;
import org.ei.bidan.adapter.SmartRegisterPaginatedAdapter;
import org.ei.bidan.bidan.provider.KartuIbuANCClientsProvider;
import org.ei.bidan.bidan.provider.KartuIbuPNCClientsProvider;
import org.ei.bidan.bidan.view.contract.BidanVillageController;
import org.ei.bidan.bidan.view.controller.KartuIbuANCRegisterController;
import org.ei.bidan.bidan.view.controller.KartuIbuPNCRegisterController;
import org.ei.bidan.bidan.view.dialog.KartuIbuANCOverviewServiceMode;
import org.ei.bidan.bidan.view.dialog.KartuIbuPNCOverviewServiceMode;
import org.ei.bidan.bidan.view.dialog.WifeAgeSort;
import org.ei.bidan.domain.form.FieldOverrides;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.util.StringUtil;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.dialog.AllClientsFilter;
import org.ei.bidan.view.dialog.DialogOption;
import org.ei.bidan.view.dialog.DialogOptionMapper;
import org.ei.bidan.view.dialog.DialogOptionModel;
import org.ei.bidan.view.dialog.DusunSort;
import org.ei.bidan.view.dialog.EditOption;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.NameSort;
import org.ei.bidan.view.dialog.OpenFormOption;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_CLOSE;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_EDIT;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_POSPARTUM_KB;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_REGISTRATION;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_VISIT;

/**
 * Created by Dimas Ciputra on 3/5/15.
 */
public class NativeKIPNCSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity{

    private SmartRegisterClientsProvider clientProvider = null;
    private KartuIbuPNCRegisterController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private DialogOptionMapper dialogOptionMapper;
    private BidanVillageController villageController;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                return new KartuIbuPNCOverviewServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.home_pnc_label) + ": ";
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(villageController.getVillagesIndonesia());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new WifeAgeSort(), new DusunSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if(clientProvider == null) {
            clientProvider = new KartuIbuPNCClientsProvider(this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KartuIbuPNCRegisterController(context.allKohort(),
                context.listCache(),context.kartuIbuPNCClientsCache());
        villageController = new BidanVillageController(context.villagesCache(), context.allKartuIbus());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    protected void startRegistration() {
        startFormActivity(AllConstants.FormNames.KARTU_IBU_PNC_OA, null, null);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    // TODO : show info of timeline event
                    // showProfileView((ECClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            SmartRegisterClient client = (SmartRegisterClient) tag;
            onShowDialogOptionSelection((EditOption)option, client, controller.getRandomNameChars(client));
        }
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.pnc_visit),
                        KARTU_IBU_PNC_VISIT, formController),
                new OpenFormOption(getString(R.string.pnc_pospartum_kb),
                        KARTU_IBU_PNC_POSPARTUM_KB, formController),
                new OpenFormOption(getString(R.string.pnc_edit),
                        KARTU_IBU_PNC_EDIT, formController),
                new OpenFormOption(getString(R.string.str_pnc_close_form),
                        KARTU_IBU_PNC_CLOSE, formController),
        };
    }
}
