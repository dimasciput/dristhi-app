package org.ei.bidan.bidan.view.activity;

import android.view.View;

import org.ei.bidan.AllConstants;
import org.ei.bidan.R;
import org.ei.bidan.adapter.SmartRegisterPaginatedAdapter;
import org.ei.bidan.bidan.provider.KBClientsProvider;
import org.ei.bidan.bidan.view.contract.BidanVillageController;
import org.ei.bidan.bidan.view.contract.KBClient;
import org.ei.bidan.bidan.view.controller.KohortKBRegisterController;
import org.ei.bidan.bidan.view.dialog.AllKBServiceMode;
import org.ei.bidan.bidan.view.dialog.WifeAgeSort;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.dialog.AllClientsFilter;
import org.ei.bidan.view.dialog.DialogOption;
import org.ei.bidan.view.dialog.DialogOptionMapper;
import org.ei.bidan.view.dialog.DialogOptionModel;
import org.ei.bidan.view.dialog.DusunSort;
import org.ei.bidan.view.dialog.EditOption;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.KBMethodSort;
import org.ei.bidan.view.dialog.NameSort;
import org.ei.bidan.view.dialog.OpenFormOption;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKBSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private KohortKBRegisterController controller;
    private DialogOptionMapper dialogOptionMapper;
    private BidanVillageController villageController;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
            new OpenFormOption(getString(R.string.str_kb_update),
                    AllConstants.FormNames.KOHORT_KB_UPDATE, formController),
            new OpenFormOption(getString(R.string.str_kb_edit),
                    AllConstants.FormNames.KOHORT_KB_EDIT, formController),
            new OpenFormOption(getString(R.string.str_kb_close),
                        AllConstants.FormNames.KOHORT_KB_CLOSE, formController),
        };
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKBServiceMode(clientsProvider());
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
                return getResources().getString(R.string.kb_register_title_in_short);
            }
        };
    }

    @Override
    public void setupViews() {
        super.setupViews();
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
                return new DialogOption[]{new NameSort(), new WifeAgeSort(), new DusunSort(),
                        new KBMethodSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new KBClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KohortKBRegisterController(context.allKartuIbus(),
                context.listCache(),context.kbClientsCache(),context.allKohort());
        villageController = new BidanVillageController(context.villagesCache(), context.allKartuIbus());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    protected void startRegistration() {
        startFormActivity(AllConstants.FormNames.KOHORT_KB_REGISTER, null, null);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout_ki:
                    showProfileView((KBClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(KBClient kartuIbuClient) {
            navigationController.startKI(kartuIbuClient.entityId());
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
}
