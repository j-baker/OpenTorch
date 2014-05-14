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
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Created by james on 2014-05-14.
 */
class Torch {
    @Nullable
    private Camera camera;
    private boolean state = false;
    private final boolean deviceHasFlash;

    Torch(final @NotNull Context activity) {
        // Reasonable to assume that device will not magically gain flash.
        final PackageManager p = activity.getPackageManager();
        deviceHasFlash =  p != null && p.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private @Nullable Camera getCamera() {
        releaseCamera();
        if (deviceHasFlash) {
            try {
                camera = Camera.open();
                if (camera != null) {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                }
            } catch (RuntimeException e) {
                camera = null;
            } catch (IOException e) {
                camera = null;
            }
        } else {
            camera = null;
        }
        return camera;
    }

    void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        camera = null;
        state = false;
    }

    private void setFlashMode(final @NotNull Camera cam) {
        final Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
    }

    void setValue(final boolean on) {
        if (on) {
            torchOn();
        } else {
            torchOff();
        }
    }

    private void torchOn() {
        final Camera cam = getCamera();
        if (cam != null) {
            setFlashMode(cam);
            cam.startPreview();
            state = true;
        }
    }

    private void torchOff() {
        releaseCamera();
    }
}
