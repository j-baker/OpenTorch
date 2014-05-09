/*
 * Copyright (c) 2014, James Baker
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.cam.jeb200.opentorch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ToggleButton;

import java.io.IOException;


public class TorchControl extends ActionBarActivity {
    private Camera camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torch_control);
    }

    private SurfaceHolder getSurfaceHolder() {
        final SurfaceView preview = (SurfaceView)findViewById(R.id.PREVIEW);
        return preview.getHolder();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ToggleButton button = (ToggleButton)findViewById(R.id.lightSwitch);
        button.setChecked(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private Camera getCamera() {
        releaseCamera();
        if (deviceHasFlash(this)) {
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                camera = null;
            }
        } else {
            camera = null;
        }
        return camera;
    }

    public void onToggleClicked(View view) {
        final boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            torchOn();
        } else {
            torchOff();
        }
    }

    private boolean deviceHasFlash(Context context) {
        final PackageManager p = context.getPackageManager();
        return p != null && p.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        camera = null;
        Log.i(getString(R.string.app_name), getString(R.string.debug_light_off));
    }

    private void setFlashMode(Camera cam) {
        final Parameters p = cam.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
    }

    private void setPreviewDisplay(Camera cam) {
        try {
            cam.setPreviewDisplay(getSurfaceHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void torchOn() {
        final Camera cam = getCamera();
        if (cam != null) {
            setFlashMode(cam);
            setPreviewDisplay(cam);
            cam.startPreview();
            Log.i(getString(R.string.app_name), getString(R.string.debug_light_on));
        } else {
            Log.w(getString(R.string.app_name), getString(R.string.debug_camera_error));
        }
    }

    private void torchOff() {
        releaseCamera();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.torch_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
