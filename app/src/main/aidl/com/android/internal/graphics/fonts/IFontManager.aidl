/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.android.internal.graphics.fonts;

interface IFontManager {

    /**
     * update Font Family from app
     * if familyName is null will load system Default familyName
     */
    void updateDefaultFontFamily(String familyName);

    String getDefaultFontFamilyName();
}