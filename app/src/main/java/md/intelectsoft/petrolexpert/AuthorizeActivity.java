package md.intelectsoft.petrolexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolexpert.Utils.LocaleHelper;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.adapters.AdapterCashListDialog;
import md.intelectsoft.petrolexpert.network.broker.Body.SendRegisterApplication;
import md.intelectsoft.petrolexpert.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolexpert.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolexpert.network.broker.Enum.BrokerServiceEnum;
import md.intelectsoft.petrolexpert.network.broker.Results.AppDataRegisterApplication;
import md.intelectsoft.petrolexpert.network.broker.Results.RegisterApplication;
import md.intelectsoft.petrolexpert.network.pe.PECErrorMessage;
import md.intelectsoft.petrolexpert.network.pe.PERetrofitClient;
import md.intelectsoft.petrolexpert.network.pe.PEServiceAPI;
import md.intelectsoft.petrolexpert.network.pe.result.CashList;
import md.intelectsoft.petrolexpert.network.pe.result.GetCashList;
import md.intelectsoft.petrolexpert.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolexpert.network.pe.result.authorizeUser.GetAuthorizeUser;
import md.intelectsoft.petrolexpert.network.pe.result.authorizeUser.UserAuth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class AuthorizeActivity extends AppCompatActivity {
    @BindView(R.id.layoutCode) TextInputLayout inputLayoutCode;
    @BindView(R.id.inputCode) TextInputEditText inputEditTextCode;

    @BindView(R.id.activateAppLayout) ConstraintLayout layoutActivate;
    @BindView(R.id.loginUserLayout) ConstraintLayout layoutAuth;

    @BindView(R.id.textCodOfUser) TextView textUserCodInput;

    String androidID, deviceName, publicIp, privateIp, deviceSN, osVersion, deviceModel, deviceId;

    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    PEServiceAPI peServiceAPI;
    Context context;
    CashList itemSelected = null;

    String codOfUser = "";

    @OnClick(R.id.registerApp) void onRegister(){
        String activationCode = inputEditTextCode.getText().toString();
        preparedActivateApp(activationCode);
    }

    @OnClick(R.id.textButtonAuth0) void onClickButton_0(){
        codOfUser = codOfUser + "0";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth1) void onClickButton_1(){
        codOfUser = codOfUser + "1";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth2) void onClickButton_2(){
        codOfUser = codOfUser + "2";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth3) void onClickButton_3(){
        codOfUser = codOfUser + "3";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth4) void onClickButton_4(){
        codOfUser = codOfUser + "4";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth5) void onClickButton_5(){
        codOfUser = codOfUser + "5";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth6) void onClickButton_6(){
        codOfUser = codOfUser + "6";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth7) void onClickButton_7(){
        codOfUser = codOfUser + "7";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth8) void onClickButton_8(){
        codOfUser = codOfUser + "8";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuth9) void onClickButton_9(){
        codOfUser = codOfUser + "9";
        textUserCodInput.append("*");
    }
    @OnClick(R.id.textButtonAuthDelete) void onClickButton_Delete(){
        String text = textUserCodInput.getText().toString();
        if(text.length() - 1 > 0){
            textUserCodInput.setText(text.substring(0, text.length() - 1));
            codOfUser = codOfUser.substring(0, codOfUser.length() - 1);
        }
        else{
            textUserCodInput.setText("");
            codOfUser = "";
        }
    }
    @OnClick(R.id.textButtonAuthClear) void onClickButton_Clear(){
        textUserCodInput.setText("");
        codOfUser = "";
    }
    @OnClick(R.id.buttonAuthToServer) void onAuthUser(){
        Log.e("PetrolExpert_BaseApp", "onAuthUserCod: "  + codOfUser);
        authorizeUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = LocaleHelper.getLanguage(this);
        setAppLocale(lang);
        setContentView(R.layout.activity_authorize);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);
        brokerServiceAPI = BrokerRetrofitClient.getApiBrokerService();

        deviceModel = Build.MODEL;
        deviceSN = Build.SERIAL;
        deviceName = Build.DEVICE;
        osVersion = Build.VERSION.RELEASE;
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceId = new UUID(androidID.hashCode(), deviceName.hashCode()).toString();
        publicIp = getPublicIPAddress(this);
        privateIp = getIPAddress(true);

        SPFHelp.getInstance().putString("deviceId", deviceId);

        String licenseId = SPFHelp.getInstance().getString("LicenseID", null);

        boolean firstStart = SPFHelp.getInstance().getBoolean("FirstStart", true);

        if(licenseId == null){
            layoutActivate.setVisibility(View.VISIBLE);
            layoutAuth.setVisibility(View.GONE);
        }
        else if (firstStart){
            layoutActivate.setVisibility(View.GONE);
            layoutAuth.setVisibility(View.VISIBLE);
        }

        inputEditTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.equals(""))
                    inputLayoutCode.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void authorizeUser() {
        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);

        Call<GetAuthorizeUser> call = peServiceAPI.authorizeUser(codOfUser);

        progressDialog.setMessage(getString(R.string.auth_user_pg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
                if(call.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        call.enqueue(new Callback<GetAuthorizeUser>() {
            @Override
            public void onResponse(Call<GetAuthorizeUser> call, Response<GetAuthorizeUser> response) {
                GetAuthorizeUser getAuthorizeUser = response.body();
                progressDialog.dismiss();
                if(getAuthorizeUser != null){
                    if(getAuthorizeUser.getErrorCode() == 0){
                        String tokenUser = getAuthorizeUser.getToken().getUid();
                        String tokenValid = getAuthorizeUser.getToken().getValidTo();
                        if (tokenValid != null) {
                            if (tokenValid != null)
                                tokenValid = tokenValid.replace("/Date(", "");
                            if (tokenValid != null)
                                tokenValid = tokenValid.substring(0, tokenValid.length() - 7);
                        }
                        UserAuth user = getAuthorizeUser.getUser();
                        String userFullName = "";
                        if(user.getSurname() != null ){
                            if(!user.getSurname().equals(""))
                                userFullName = " " + user.getSurname();
                        }

                        long timeValid = Long.parseLong(tokenValid);
                        SPFHelp.getInstance().putLong("TokenValid", timeValid);
                        SPFHelp.getInstance().putString("TokenId", tokenUser);
                        SPFHelp.getInstance().putString("Owner", user.getName() + userFullName);
                        SPFHelp.getInstance().putString("OwnerId", user.getUserID());
                        SPFHelp.getInstance().putString("UserCodeAuth", codOfUser);

                        if(SPFHelp.getInstance().getString("CashId", null) == null){
                            getCashList(tokenUser);
                        }
                        else{
                            if(itemSelected == null){
                                itemSelected = new CashList();
                            }
                            itemSelected.setCashID(SPFHelp.getInstance().getString("CashId",null));
                            registerDeviceToBack(tokenUser);
                        }
                    }
                    else
                        showErrorDialogAuthUser(getString(R.string.error_auth_user_msg) + getAuthorizeUser.getErrorMessage());
                }
                else showErrorDialogAuthUser(getString(R.string.error_auth_user_empty));
            }

            @Override
            public void onFailure(Call<GetAuthorizeUser> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogAuthUser(getString(R.string.error_auth_user_failure)+ t.getMessage());
            }
        });
    }

    private void getCashList(String token) {
        Call<GetCashList> call = peServiceAPI.getCashList(token);
        progressDialog.setMessage(getString(R.string.obtain_cash_list_pg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_button), (dialog, which) -> {
            call.cancel();
            if(call.isCanceled())
                dialog.dismiss();
        });
        progressDialog.show();

        call.enqueue(new Callback<GetCashList>() {
            @Override
            public void onResponse(Call<GetCashList> call, Response<GetCashList> response) {
                GetCashList getCashList = response.body();
                progressDialog.dismiss();

                if(getCashList != null){
                    if(getCashList.getErrorCode() == 0){
                        List<CashList> cashLists = getCashList.getCashList();

                        if(cashLists != null && cashLists.size() > 0){
                            AdapterCashListDialog adapter = new AdapterCashListDialog(context,R.layout.item_list_cash, cashLists);


                            new MaterialAlertDialogBuilder(context,  R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle(getString(R.string.select_workplace_title))
                                    .setCancelable(false)
                                    .setSingleChoiceItems(adapter, -1, (dialog, which) -> {
                                        itemSelected = adapter.getItem(which);
                                        Log.e("PetrolExpert_BaseApp", "onClick: " + itemSelected.getCashName());
                                    })
                                    .setPositiveButton(getString(R.string.select_button), (dialog, which) -> {
                                        if(itemSelected != null){
                                            Log.e("PetrolExpert_BaseApp", "onClick: " + itemSelected.getCashName());
                                            registerDeviceToBack(SPFHelp.getInstance().getString("TokenId",""));
                                        }

                                    })
                                    .setNegativeButton(getString(R.string.cancel_button), (dialogInterface, i) -> {

                                    })
                                    .show();
                        }

                    }
                    else{
                        new MaterialAlertDialogBuilder(context,  R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.attention_dialog_title))
                                .setCancelable(false)
                                .setMessage("Ops!" + PECErrorMessage.getErrorMessage(getCashList.getErrorCode()))
                                .setPositiveButton(getString(R.string.ok_button), (dialog, which) -> {

                                })
                                .setNeutralButton(getString(R.string.retry_button), (dialogInterface, i) -> {
                                    getCashList(token);
                                })
                                .show();
                    }
                }
                else{
                    new MaterialAlertDialogBuilder(context,  R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle(getString(R.string.attention_dialog_title))
                            .setCancelable(false)
                            .setMessage(getString(R.string.response_from_broker_is_null))
                            .setPositiveButton(getString(R.string.ok_button), (dialog, which) -> {

                            })
                            .setNeutralButton(getString(R.string.retry_button), (dialogInterface, i) -> {
                                getCashList(token);
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<GetCashList> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(context,  R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle(getString(R.string.attention_dialog_title))
                        .setCancelable(false)
                        .setMessage(getString(R.string.ops_failure_get_cash_list) + t.getMessage())
                        .setPositiveButton(getString(R.string.ok_button), (dialog, which) -> {

                        })
                        .setNeutralButton(getString(R.string.retry_button), (dialogInterface, i) -> {
                            getCashList(token);
                        })
                        .show();
            }
        });
    }

    private void registerDeviceToBack(String token) {

        Call<RegisterDevice> call = peServiceAPI.registerDevice(deviceId, "Android " + deviceModel, itemSelected.getCashID(), token);

        progressDialog.setMessage(getString(R.string.register_device_pg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_button), (dialog, which) -> {
            call.cancel();
            if (call.isCanceled())
                dialog.dismiss();
        });
        progressDialog.show();

        call.enqueue(new Callback<RegisterDevice>() {
            @Override
            public void onResponse(Call<RegisterDevice> call, Response<RegisterDevice> response) {
                progressDialog.dismiss();
                RegisterDevice device = response.body();
                if(device != null)
                    if(device.getNoError() == 0 && device.getRegistred()){
                        SPFHelp.getInstance().putInt("RegisteredNumber", device.getRegistredNumber());
                        if(SPFHelp.getInstance().getString("CashId", null) == null){
                            SPFHelp.getInstance().putString("Cash", itemSelected.getCashName());
                            SPFHelp.getInstance().putString("StationName", itemSelected.getStationName());
                            SPFHelp.getInstance().putString("StationAddress", itemSelected.getStationAddress());
                            SPFHelp.getInstance().putString("CashId", itemSelected.getCashID());
                        }
                        SPFHelp.getInstance().putBoolean("FirstStart", false);

                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }
                    else showErrorDialog(getString(R.string.device_not_registered) + PECErrorMessage.getErrorMessage(device.getNoError()));
                else showErrorDialog(getString(R.string.device_not_registered_not_response));
            }

            @Override
            public void onFailure(Call<RegisterDevice> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialog(getString(R.string.device_not_registered_failure) + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                    registerDeviceToBack(SPFHelp.getInstance().getString("TokenId",""));
                }))
                .show();
    }

    private void showErrorDialogAuthUser (String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {

                })
                .show();
    }

    private void preparedActivateApp(String activationCode) {
        if(activationCode.equals(""))
            inputLayoutCode.setError(getString(R.string.please_input_the_field));
        else{
            //data send to register app in broker server
            SendRegisterApplication registerApplication = new SendRegisterApplication();

            String ids = new UUID(androidID.hashCode(),androidID.hashCode()).toString();
            registerApplication.setDeviceID(ids);
            registerApplication.setDeviceModel(deviceModel);
            registerApplication.setDeviceName(deviceName);
            registerApplication.setSerialNumber(deviceSN);
            registerApplication.setPrivateIP(privateIp);
            registerApplication.setPublicIP(publicIp);
            registerApplication.setOSType(BrokerServiceEnum.Android);
            registerApplication.setApplicationVersion(getAppVersion(this));
            registerApplication.setProductType(BrokerServiceEnum.CashPetrolExpert);
            registerApplication.setOSVersion(osVersion);
            registerApplication.setLicenseActivationCode(activationCode);

            registerApplicationToBroker(registerApplication, activationCode);
        }
    }

    private void registerApplicationToBroker(SendRegisterApplication registerApplication, String activationCode) {
        Call<RegisterApplication> registerApplicationCall = brokerServiceAPI.registerApplication(registerApplication);

        progressDialog.setMessage(getString(R.string.register_device_pg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerApplicationCall.cancel();
                if(registerApplicationCall.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        registerApplicationCall.enqueue(new Callback<RegisterApplication>() {
            @Override
            public void onResponse(Call<RegisterApplication> call, Response<RegisterApplication> response) {
                RegisterApplication result = response.body();

                if (result == null){
                    progressDialog.dismiss();
                    Toast.makeText(context, getString(R.string.response_from_broker_is_null), Toast.LENGTH_SHORT).show();
                }
                else{
                    if(result.getErrorCode() == 0) {
                        AppDataRegisterApplication appDataRegisterApplication = result.getAppData();
                        String logo = null;

                        if(appDataRegisterApplication.getLogo() != null && !appDataRegisterApplication.getLogo().equals("")){
                            String photo = appDataRegisterApplication.getLogo();
                            if(photo != null && photo.length() > 0){
                                photo = photo.replace("data:image/","");
                                String typePhoto = photo.substring(0,3);

                                switch (typePhoto) {
                                    case "jpe":
                                        photo = photo.replace("jpeg;base64,", "");
                                        break;
                                    case "jpg":
                                        photo = photo.replace("jpg;base64,", "");
                                        break;
                                    case "png":
                                        photo = photo.replace("png;base64,", "");
                                        break;
                                }

                                logo = photo;
                            }
                        }

                        //if app registered successful , save installation id and company name
                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID", appDataRegisterApplication.getLicenseID());
                        licenseData.put("LicenseCode", appDataRegisterApplication.getLicenseCode());
                        licenseData.put("CompanyName", appDataRegisterApplication.getCompany());
                        licenseData.put("CompanyIDNO", appDataRegisterApplication.getIDNO());
                        licenseData.put("CompanyLogo", logo == null ? "" : logo);
                        licenseData.put("LicenseActivationCode", activationCode);

                        SPFHelp.getInstance().putStrings(licenseData);

                        layoutActivate.setVisibility(View.GONE);
                        layoutAuth.setVisibility(View.VISIBLE);
                        //after register app ,get URI for accounting system on broker server
                        progressDialog.dismiss();

                        if(appDataRegisterApplication.getURI() != null && !appDataRegisterApplication.getURI().equals("") && appDataRegisterApplication.getURI().length() > 5){
                            long nowDate = new Date().getTime();
                            String serverStringDate = appDataRegisterApplication.getServerDateTime();
                            serverStringDate = serverStringDate.replace("/Date(","");
                            serverStringDate = serverStringDate.replace("+0200)/","");
                            serverStringDate = serverStringDate.replace("+0300)/","");

                            long serverDate = Long.parseLong(serverStringDate);

                            SPFHelp.getInstance().putString("URI", appDataRegisterApplication.getURI());
                            SPFHelp.getInstance().putLong("DateReceiveURI", nowDate);
                            SPFHelp.getInstance().putLong("ServerDateTime", serverDate);
                        }
                        else{
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle(getString(R.string.url_not_set_title))
                                    .setMessage(getString(R.string.app_not_configured))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                        finish();
                                    })
                                    .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                                        registerApplicationToBroker(registerApplication, activationCode);
                                    }))
                                    .show();

                        }
                    }
                    else {
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.attention_dialog_title))
                                .setMessage("Ops!" + PECErrorMessage.getErrorMessage(result.getErrorCode()))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                    finish();
                                })
                                .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                                    registerApplicationToBroker(registerApplication, activationCode);
                                }))
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApplication> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, getString(R.string.fail_register_app) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    private String getPublicIPAddress(Context context) {
        //final NetworkInfo info = NetworkUtils.getNetworkInfo(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();

        RunnableFuture<String> futureRun = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if ((info != null && info.isAvailable()) && (info.isConnected())) {
                    StringBuilder response = new StringBuilder();

                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) (
                                new URL("http://checkip.amazonaws.com/").openConnection());
                        urlConnection.setRequestProperty("User-Agent", "Android-device");
                        //urlConnection.setRequestProperty("Connection", "close");
                        urlConnection.setReadTimeout(1000);
                        urlConnection.setConnectTimeout(1000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Content-type", "application/json");
                        urlConnection.connect();

                        int responseCode = urlConnection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {

                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                        }
                        urlConnection.disconnect();
                        return response.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //Log.w(TAG, "No network available INTERNET OFF!");
                    return null;
                }
                return null;
            }
        });

        new Thread(futureRun).start();

        try {
            return futureRun.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getAppVersion(Context context){
        String result = "";

        try{
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            result = result.replaceAll("[a-zA-Z] |-","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }
}