package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class ChoiceProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvTitle;
    private RecyclerView rvProduct;
    private Button btnSubmit ;

    private ShopCartActivity mActivity;
    private List<Product> listProducts = new ArrayList<>();
    private CartProductDialogAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart_choice_product,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.PRODUCTGROUP);
        List<Product> all = Product.getProductList();
        try {
            for (int i=0 ; i<all.size(); i++){
                Product product = all.get(i);
                product.put("checked", false);

                listProducts.add(product);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createRVProduct(listProducts, new ProductGroup(bundle));

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (ShopCartActivity) getActivity();
        tvTitle = view.findViewById(R.id.cart_choice_header_title);
        rvProduct = view.findViewById(R.id.cart_choice_rvproduct);
        btnSubmit = (Button) view.findViewById(R.id.cart_choice_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.cart_choice_submit:
                submitProduct();
                break;

        }
    }

    private void createRVProduct(List<Product> list, ProductGroup group){
        adapter = new CartProductDialogAdapter(list, group);
        rvProduct.setAdapter(adapter);
        rvProduct.setHasFixedSize(true);
        rvProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);
    }

    private void submitProduct(){
        List<Product> listChecked = new ArrayList<Product>();
        for (int i=0; i<adapter.getAllData().size(); i++){
            if (adapter.getAllData().get(i).getBoolean("checked")){
                listChecked.add(adapter.getAllData().get(i));
            }
        }
        mActivity.updatelistProduct(listChecked, false);
        getActivity().getSupportFragmentManager().popBackStack();

    }


}
