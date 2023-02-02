/**
 * Copyright (c) 2012-2017, Smart Engines Ltd
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Smart Engines Ltd nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartengines;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import biz.smartengines.smartid.swig.RecognitionResult;
import biz.smartengines.smartid.swig.StringVector2d;


/**
 * Main sample activity for documents recognition with Smart IDReader Android SDK
 */
public class ScanSmartActivity extends AppCompatActivity
        implements SmartIDCallback, View.OnClickListener {

    final static String nameVariableKey = "DOC_VARIABLE";
    private static SmartIDView view = null;
    private final int REQUEST_CAMERA_PERMISSION = 1;
    boolean processing = false; ///< Whether an active recognition session is processing frames
    /** Important!
     Setting enabled document types for the recognition session
     according to available document types for your delivery
     these types will be passed to SessionSettings
     with which RecognitionEngine's SpawnSession(...) method is called.
     Internally you can specify a concrete document type or a
     wildcard expression (for convenience) to enable or disable multiple types.
     If exception is thrown please read the exception message. */

    // Enabled document mask (all documents by default, unusable with some bundles)
    String document_mask = Constant.ID_KENYAN;
    String document_name = "Kenyan ID";
    // Recognition session timeout
    String time_out = "5.0";
    private Button button;
    private ImageButton selector;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ScanSmartActivity.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(nameVariableKey, document_mask);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        document_mask = savedInstanceState.getString(nameVariableKey);
        TextView Doc = findViewById(R.id.textViewDoc);
        Doc.setText(document_mask);
    }

    public String[] SelectDocs() {

        StringVector2d selectdocs = view.getDocumentsList();

        ArrayList<String> docs = new ArrayList<>();

        for (int i = 0; i < selectdocs.size(); i++) {
            for (int j = 0; j < selectdocs.get(i).size(); j++) {
                docs.add(selectdocs.get(i).get(j));

            }
        }

        return docs.toArray(new String[docs.size()]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_smart);

        button = findViewById(R.id.button);
        selector = findViewById(R.id.selector);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Scan ID");
        findViewById(R.id.img_back).setOnClickListener(v -> onBackPressed());

        view = new SmartIDView();
        initEngine();

        if (permission(Manifest.permission.CAMERA)) {
            request(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void initEngine() {
        try {
            view.initializeEngine(this, this);
        } catch (Exception e) {
            Log.d("smartid", "Engine initialization failed: " + e);
        }

        SurfaceView preview = findViewById(R.id.preview);
        RelativeLayout drawing = findViewById(R.id.drawing);
        view.setSurface(preview, drawing);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            button.setOnClickListener(null); // Disable button
            selector.setOnClickListener(null);

            if (!processing) {
                view.startRecognition(document_mask, time_out);
            } else {
                view.stopRecognition();
            }
        }

        if (v.getId() == R.id.selector) {
            showList();
        }
    }

    void toast(final String message) {
        runOnUiThread(() -> {
            Toast t = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        });
    }

    public boolean permission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result != PackageManager.PERMISSION_GRANTED;
    }

    public void request(String permission, int request_code) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, request_code);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            boolean granted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) { // Permission is granted
                    granted = true;
                    break;
                }
            }
            if (granted) {
                view.updatePreview();
            } else {
                toast("Please enable Camera permission.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void initialized(boolean engine_initialized) {
        if (engine_initialized) {
            button.setOnClickListener(this);
            selector.setOnClickListener(this);
            showNameDoc();
        }
    }

    @Override
    public void recognized(RecognitionResult result) {
        // The result is terminal when the engine decides that the recognition result has had
        // enough information and ready to produce result, or when the session is timed out
        if (result.IsTerminal()) {
            view.stopRecognition();

            if (result.GetDocumentType().isEmpty()) {
                toast("Document not found."); // No result has been returned on any frame
                return;
            }

            MainResultStore.instance.setResult(result);
            for (String name : MainResultStore.instance.getFieldNames()) {
                Log.e("FieldName", name);
            }
            MainResultStore.instance.setScannedIDData(getDataAccordingToDocument());
            setResult(RESULT_OK);
            finish();
        }
    }

    private ScannedIDData getDataAccordingToDocument() {
        switch (MainResultStore.instance.getDocumentType()) {
            case Constant.ID_KENYAN:
                //document type, birth_date, birth_place, gender, id_number, issue_date, issue_place, name, number, photo, signature;
                ScannedIDData scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("id_number");
                scannedIDData.nationality = "Kenyan";
                scannedIDData.name = MainResultStore.instance.getStringValue("name");
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                scannedIDData.birth_date = MainResultStore.instance.getStringValue("birth_date");
                scannedIDData.birth_place =MainResultStore.instance.getStringValue("birth_place");
                scannedIDData.issue_date = MainResultStore.instance.getStringValue("issue_date");
                scannedIDData.issue_place = MainResultStore.instance.getStringValue("issue_place");
                scannedIDData.serialNumber = MainResultStore.instance.getStringValue("number");

                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.ID_UGANDA:
                //document type, birth_date, expiry_date, gender, id_number, last_name, last_name_eng, name, name_eng, nationality, nationality_eng, number, photo, signature;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Ugandan";
                scannedIDData.name = MainResultStore.instance.getStringValue("name").concat(" ").concat(MainResultStore.instance.getStringValue("last_name"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.ID_TANZANIA:
                //document type, expiry_date, first_name, gender, last_name, middle_name, name, number, photo, signature;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Tanzanian";
                scannedIDData.name = MainResultStore.instance.getStringValue("first_name").concat(" ").concat(MainResultStore.instance.getStringValue("middle_name")).concat(" ").concat(MainResultStore.instance.getStringValue("last_name"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.ID_RWANDA:
                //document type, birth_date, designation, full_name, gender, issue_place, issue_place_eng, last_name, name, nationality, number, signature;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Rwandan";
                scannedIDData.name = MainResultStore.instance.getStringValue("full_name");
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                return scannedIDData;

            case Constant.ID_UAE:
                //document type, id_number, name, name_eng, nationality, nationality_eng, photo;

                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("id_number");
                scannedIDData.nationality = "Emirati, Emirian, Emiri";
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.ID_AADHAAR:
                //document type, birth_date, gender, name_eng, name_eng_ws, number;
                scannedIDData = new ScannedIDData();
                scannedIDData.nationality = "Indian";
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng");
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                return scannedIDData;

            case Constant.ID_PANCARD:
                //document type, birth_date, father, name, number, photo;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Indian";
                scannedIDData.name = MainResultStore.instance.getStringValue("name");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.PASSPORT_KENYAN:
                //document type, authority, authority_eng, birth_date, birth_place, birth_place_eng, full_mrz, gender, issue_date, last_name, last_name_eng, mrz_birth_date, mrz_doc_type_code, mrz_expiry_date, mrz_gender, mrz_issuer, mrz_last_name, mrz_line1, mrz_line2, mrz_name, mrz_nationality, mrz_number, mrz_opt_data_2, name, name_eng, nationality, nationality_eng, photo, signature,
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("mrz_number");
                scannedIDData.nationality = "Kenyan";
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng").concat(" ").concat(MainResultStore.instance.getStringValue("last_name_eng"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.PASSPORT_UGANDA:
                //document type, authority, authority_eng, birth_date, birth_place, birth_place_eng, expiry_date, full_mrz, gender, issue_date, last_name, last_name_eng, mrz_birth_date, mrz_doc_type_code, mrz_expiry_date, mrz_gender, mrz_issuer, mrz_last_name, mrz_line1, mrz_line2, mrz_name, mrz_nationality, mrz_number, mrz_opt_data_2, name, name_eng, nationality, nationality_eng, number, photo, profession, profession_eng, signature, type;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Ugandan";
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng").concat(" ").concat(MainResultStore.instance.getStringValue("last_name_eng"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;

            case Constant.PASSPORT_TANZANIA:
                //document type, authority, authority_eng, birth_place, birth_place_eng, expiry_date, gender, issue_date, last_name, last_name_eng, name, name_eng, nationality, nationality_eng, photo, signature, type,
            case Constant.PASSPORT_TANZANIA_OLD:
                //document type, authority, authority_eng, birth_date, birth_place, birth_place_eng, expiry_date, full_mrz, gender, issue_date, last_name, last_name_eng, mrz_birth_date, mrz_doc_type_code, mrz_expiry_date, mrz_gender, mrz_issuer, mrz_last_name, mrz_line1, mrz_line2, mrz_name, mrz_nationality, mrz_number, mrz_opt_data_2, name, name_eng, nationality, nationality_eng, number, photo, signature, type;

                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Tanzanian";
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng").concat(" ").concat(MainResultStore.instance.getStringValue("last_name_eng"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                if (MainResultStore.instance.isFieldImage("photo")) {
                    scannedIDData.userImage = MainResultStore.instance.getImageValue("photo");
                }
                return scannedIDData;
                    /*case Constant.PASSPORT_RWANDA:
                    case Constant.PASSPORT_UAE:*/

            case Constant.PASSPORT_INDIA:
                //document type, birth_date, birth_place, birth_place_eng, expiry_date, gender, issue_date, issue_place, issue_place_eng, last_name, last_name_eng, name, name_eng, nationality, nationality_eng, number, signature, type;
                scannedIDData = new ScannedIDData();
                scannedIDData.idNumber = MainResultStore.instance.getStringValue("number");
                scannedIDData.nationality = "Indian";
                scannedIDData.name = MainResultStore.instance.getStringValue("name_eng").concat(" ").concat(MainResultStore.instance.getStringValue("last_name_eng"));
                scannedIDData.gender = MainResultStore.instance.getStringValue("gender");
                return scannedIDData;

            default:
                return MainResultStore.instance.getScannedIDData();
        }
    }

    @Override
    public void started() {
        processing = true;
        button.setText("CANCEL");
        button.setOnClickListener(this);
        selector.setOnClickListener(null);
    }

    @Override
    public void stopped() {
        processing = false;
        button.setText("START");
        button.setOnClickListener(this);
        selector.setOnClickListener(this);
    }

    @Override
    public void error(String message) {
        toast(message);
    }

    private void showNameDoc() {
        TextView Doc = findViewById(R.id.textViewDoc);
        Doc.setText("Document: " + document_name);
    }

    private void showList() {
        //final String[] sdocs = SelectDocs();
        final String[] sDocs = {"ken.id.type1", "ken.passport.type1", "uga.id.type1", "uga.passport.type1", "tza.id.type1", "tza.passport.type1", "tza.passport.type2",
                "rwa.id.type1", /*"rwa.passport.type1",*/ "are.id.type1", /*"are.passport.type1",*/ "ind.aadhaar.type1", "ind.pancard.type1",
                "ind.passport.type1"};

        final String[] sTitle = {"Kenyan ID", "Kenyan Passport", "Uganda ID", "Uganda Passport", "Tanzania ID", "Tanzania Passport New", "Tanzania Passport Old",
                "Rwanda ID", /*"Rwanda Passport",*/ "UAE ID", /*"UAE Passport",*/ "Aadhaar ID", "Pan Card",
                "India Passport"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanSmartActivity.this);
        builder.setTitle("Choose document");
        builder.setItems(sTitle, (dialog, item) -> {
            document_mask = sDocs[item];
            document_name = sTitle[item];
            showNameDoc();
        });

        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (view!=null){
            view.closeCamera();
            view.stopRecognition();
            view = null;
        }
    }
}
