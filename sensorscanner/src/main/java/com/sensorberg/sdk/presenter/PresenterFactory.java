package com.sensorberg.sdk.presenter;

public class PresenterFactory {
    public static Presentation newPresentation(Presenter presenter, PresentationConfiguration presentationConf) {
        return new Presentation(presenter, presentationConf);
    }
}
