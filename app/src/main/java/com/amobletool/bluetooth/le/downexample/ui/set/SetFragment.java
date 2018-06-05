package com.amobletool.bluetooth.le.downexample.ui.set;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;
import com.amobletool.bluetooth.le.downexample.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;


public class SetFragment extends MVPBaseFragment<SetContract.View, SetPresenter>
        implements SetContract.View, View.OnClickListener {

    private Button btn_setTime;
    private EditText et_bili;
    private Button btn_bili;
    private Button btn_ziku;
    private Button btn_clean;
    private ProgressDialog progressDialog;
    private Button btn_clean_flash;
    private AlertDialog alertDialog;
    private EditText et_bili_least;
    private Button btn_bili_least;
    private Button btn_logo;
    private EditText et_logo;
    private Button btn_worker;
    private EditText et_worker;
    private TextView tv_version;

    @Override
    public int getLayout() {
        return R.layout.fragment_set;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        initView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventMain(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            if (!result) {
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        btn_setTime = (Button) view.findViewById(R.id.btn_setTime);
        btn_setTime.setOnClickListener(this);
        et_bili = (EditText) view.findViewById(R.id.et_bili);
        btn_bili = (Button) view.findViewById(R.id.btn_bili);
        btn_bili.setOnClickListener(this);
        btn_ziku = (Button) view.findViewById(R.id.btn_ziku);
        btn_ziku.setOnClickListener(this);
        btn_clean = (Button) view.findViewById(R.id.btn_clean);
        btn_clean.setOnClickListener(this);
        btn_clean_flash = (Button) view.findViewById(R.id.btn_clean_flash);
        btn_clean_flash.setOnClickListener(this);
        et_bili_least = (EditText) view.findViewById(R.id.et_bili_least);
        btn_bili_least = (Button) view.findViewById(R.id.btn_bili_least);
        btn_bili_least.setOnClickListener(this);
        btn_logo = (Button) view.findViewById(R.id.btn_logo);
        btn_logo.setOnClickListener(this);
        et_logo = (EditText) view.findViewById(R.id.et_logo);
        btn_worker = (Button) view.findViewById(R.id.btn_worker);
        btn_worker.setOnClickListener(this);
        et_worker = (EditText) view.findViewById(R.id.et_worker);
        tv_version = (TextView) view.findViewById(R.id.tv_version);
        tv_version.setText("V" + Utils.getVerName(getActivity()));
    }

    @Override
    public void onClick(View v) {
        boolean cn = getActivity().getResources().getConfiguration().locale.getCountry().equals("CN");
        switch (v.getId()) {
            case R.id.btn_setTime:
                mPresenter.setTime();
                break;
            case R.id.btn_bili:
                mPresenter.setBili(et_bili.getText().toString());
                break;
            case R.id.btn_ziku:
                progressDialog = new ProgressDialog(getActivity());
                if (cn) {
                    progressDialog.setMessage("设置中...");
                } else {
                    progressDialog.setMessage("Setting...");
                }
                progressDialog.setCancelable(false);
                progressDialog.show();
                mPresenter.setZiKu(getActivity(), progressDialog);
                break;
            case R.id.btn_clean:
                mPresenter.setClean();
                break;
            case R.id.btn_logo:
                String logo = et_logo.getText().toString();
                if (TextUtils.isEmpty(logo)) {
                    if (cn) {
                        Toast.makeText(getActivity(), "LOGO不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Can not be empty.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                byte[] gb18030 = new byte[0];
                try {
                    gb18030 = logo.getBytes("gb18030");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (gb18030.length > 8) {
                    if (cn) {
                        Toast.makeText(getActivity(), "LOGO不能超过四个中文", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "No more than eight Words.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
//                boolean chineseName = StringUtils.isChinese(logo);
//                if (!chineseName) {
//                    if (cn) {
//                        Toast.makeText(getActivity(), "LOGO必须为中文", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Must be Chinese", Toast.LENGTH_LONG).show();
//                    }
//                    return;
//                }

                progressDialog = new ProgressDialog(getActivity());
                if (cn) {
                    progressDialog.setMessage("设置中...");
                } else {
                    progressDialog.setMessage("Setting...");
                }
                progressDialog.setCancelable(false);
                progressDialog.show();
                mPresenter.setLogo(getActivity(), progressDialog, logo);
                break;

            case R.id.btn_worker:
                String worker = et_worker.getText().toString();
                if (TextUtils.isEmpty(worker)) {
                    if (cn) {
                        Toast.makeText(getActivity(), "操作员姓名不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Can not be empty.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                byte[] gb18030s = new byte[0];
                try {
                    gb18030s = worker.getBytes("gb18030");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (gb18030s.length > 8) {
                    if (cn) {
                        Toast.makeText(getActivity(), "LOGO不能超过四个中文", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "No more than eight Words.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
//                boolean chineseWorker = StringUtils.isChinese(worker);
//                if (!chineseWorker) {
//                    if (cn) {
//                        Toast.makeText(getActivity(), "操作员姓名必须为中文", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Must be Chinese", Toast.LENGTH_LONG).show();
//                    }
//                    return;
//                }

                progressDialog = new ProgressDialog(getActivity());
                if (cn) {
                    progressDialog.setMessage("设置中...");
                } else {
                    progressDialog.setMessage("Setting...");
                }

                progressDialog.setCancelable(false);
                progressDialog.show();
                mPresenter.setWorkerName(getActivity(), progressDialog, worker);
                break;

            case R.id.btn_clean_flash:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText editText = new EditText(getActivity());
                builder.setView(editText);
                if (cn) {
                    builder.setMessage("设置密码");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = editText.getText().toString();
                            if ("0000".equals(text)) {
                                mPresenter.setCleanFlash();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    builder.setMessage("Password");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = editText.getText().toString();
                            if ("0000".equals(text)) {
                                mPresenter.setCleanFlash();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "wrong password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.btn_bili_least:
                mPresenter.setLeastBili(et_bili_least.getText().toString());
                break;
        }
    }

}
