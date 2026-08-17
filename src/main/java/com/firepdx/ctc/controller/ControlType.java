package com.firepdx.ctc.controller;

import com.firepdx.ctc.input.JoystickInputs;

public enum ControlType
{
    KEYBOARD_MOUSE,
    JOYSTICK,
    CUSTOM_0,
    CUSTOM_1;

    public boolean IsAdapted()
    {
        switch (this)
        {
            case KEYBOARD_MOUSE:
                return !JoystickInputs.HasJoystick();
            case JOYSTICK:
                return JoystickInputs.HasJoystick();
            default:
                return true;
        }
    }
}