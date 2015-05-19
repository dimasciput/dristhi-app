package org.ei.bidan.bidan.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import org.ei.bidan.AllConstants;
import org.ei.bidan.R;
import org.ei.bidan.adapter.SmartRegisterPaginatedAdapter;
import org.ei.bidan.bidan.provider.KartuIbuANCClientsProvider;
import org.ei.bidan.bidan.view.controller.KartuIbuANCRegisterController;
import org.ei.bidan.bidan.view.dialog.EstimatedDateOfDeliverySortKIANC;
import org.ei.bidan.bidan.view.dialog.KartuIbuANCOverviewServiceMode;
import org.ei.bidan.domain.form.FieldOverrides;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.util.StringUtil;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.dialog.AllClientsFilter;
import org.ei.bidan.view.dialog.DialogOption;
import org.ei.bidan.view.dialog.DialogOptionModel;
import org.ei.bidan.view.dialog.DusunSort;
import org.ei.bidan.view.dialog.EditOption;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.HighRiskSort;
import org.ei.bidan.view.dialog.NameSort;
import org.ei.bidan.view.dialog.OpenFormOption;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;

import java.util.Collections;
import java.util.List;

import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_CLOSE;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_EDIT;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_PARTOGRAF;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_RENCANA_PERSALINAN;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_ANC_VISIT;
import static org.ei.bidan.AllConstants.FormNames.KARTU_IBU_PNC_REGISTRATION;

/**
 * Created by Dimas Ciputra on 3/5/15.
 */
public class NativeKIANCSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity{

    private SmartRegisterClientsProvider clientProvider = null;
    private KartuIbuANCRegisterController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private final int numPickList = 3;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                return new KartuIbuANCOverviewServiceMode(clientsProvider());
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
                return getResources().getString(R.string.anc_label);
            }
        };
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        String name;
        CharSequence listNames[];

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            List<String> randomName = context.allKohort().randomANCName(numPickList);

            final DialogOption _option = option;
            final Object _tag = tag;

            name = StringUtil.humanize(((SmartRegisterClient) tag).name());

            if (!randomName.contains(name)) {
                randomName = randomName.subList(0, numPickList - 1);
                randomName.add(name);
                Collections.shuffle(randomName);
            }

            listNames = randomName.toArray(new CharSequence[randomName.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(NativeKIANCSmartRegisterActivity.this);
            builder.setTitle(R.string.title_double_selection);

            builder.setItems(listNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listNames[which].equals(name)) {
                        onEditSelection((EditOption) _option, (SmartRegisterClient) _tag);
                    }
                }
            });
            builder.show();
        }
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_rencana_persalinan_anc_form),
                        KARTU_IBU_ANC_RENCANA_PERSALINAN, formController),
                new OpenFormOption(getString(R.string.str_register_anc_partograf_form),
                        KARTU_IBU_ANC_PARTOGRAF, formController),
                new OpenFormOption("ANC Visit",
                        KARTU_IBU_ANC_VISIT, formController),
                new OpenFormOption(getString(R.string.str_register_pnc_form),
                        KARTU_IBU_PNC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.anc_edit),
                        KARTU_IBU_ANC_EDIT, formController),
                new OpenFormOption(getString(R.string.str_register_anc_close_form),
                        KARTU_IBU_ANC_CLOSE, formController),
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{new AllClientsFilter()};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new EstimatedDateOfDeliverySortKIANC(), new HighRiskSort()
                , new DusunSort()};
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
            clientProvider = new KartuIbuANCClientsProvider(this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KartuIbuANCRegisterController(context.allKohort(),
                context.listCache(),context.kartuIbuANCClientsCache());
    }

    @Override
    protected void startRegistration() {
        FieldOverrides fieldOverrides = new FieldOverrides(context.anmLocationController().getLocationJSON());
        startFormActivity(AllConstants.FormNames.KARTU_IBU_ANC_OA, null, fieldOverrides.getJSONString());
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
}
