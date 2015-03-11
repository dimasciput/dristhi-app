package org.ei.drishti.bidan.view.customControls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ei.drishti.AllConstants;
import org.ei.drishti.R;
import org.ei.drishti.bidan.view.contract.KartuIbuANCSmartRegisterClient;
import org.ei.drishti.bidan.view.contract.KartuIbuClient;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.ei.drishti.view.viewHolder.ProfilePhotoLoader;

/**
 * Created by Dimas Ciputra on 3/6/15.
 */
public class BidanClientProfileView extends RelativeLayout {

    private ImageView imgProfileView;
    private TextView txtNameView;
    private TextView txtAgeView;
    private TextView txtVillageNameView;
    private TextView txtHusbandName;

    @SuppressWarnings("UnusedDeclaration")
    public BidanClientProfileView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public BidanClientProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BidanClientProfileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        this.imgProfileView = (ImageView) findViewById(R.id.img_profile);
        this.txtNameView = (TextView) findViewById(R.id.wife_name);
        this.txtAgeView = (TextView) findViewById(R.id.wife_age);
        this.txtHusbandName = (TextView) findViewById(R.id.txt_husband_name);
        this.txtVillageNameView = (TextView) findViewById(R.id.txt_village_name);
    }

    public void bindData(SmartRegisterClient client, ProfilePhotoLoader photoLoader) {
        this.imgProfileView.setBackground(photoLoader.get(client));
        this.txtNameView.setText(client.wifeName() != null ? client.wifeName() : "");
        this.txtVillageNameView.setText(client.village() != null ? client.village() : "");
        this.txtAgeView.setText(client.ageInString() != null ? client.ageInString() : "");
        this.txtHusbandName.setText(client.husbandName() != null ? client.husbandName() : "-");
    }

    private boolean isAnANCClient(SmartRegisterClient client) {
        return client instanceof KartuIbuANCSmartRegisterClient;
    }

    private String getOutOfAreaText(String locationStatus) {
        return isOutOfArea(locationStatus) ? org.ei.drishti.Context.getInstance().getStringResource(R.string.str_out_of_area) : "";
    }

    private boolean isOutOfArea(String locationStatus) {
        return AllConstants.OUT_OF_AREA.equalsIgnoreCase(locationStatus);
    }
}
