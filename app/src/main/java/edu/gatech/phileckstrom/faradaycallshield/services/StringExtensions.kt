package edu.gatech.phileckstrom.faradaycallshield.services

import android.net.Uri

fun String.removeTelPrefix() = this.replace("tel:", "")
fun String.parseCountryCode(): String = Uri.decode(this)