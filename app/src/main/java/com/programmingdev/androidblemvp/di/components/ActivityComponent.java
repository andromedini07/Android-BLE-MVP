package com.programmingdev.androidblemvp.di.components;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;

import com.programmingdev.androidblemvp.di.modules.PresenterModule;
import com.programmingdev.androidblemvp.di.scopes.PerActivity;
import com.programmingdev.androidblemvp.main.IMainPresenter;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class}, modules = {PresenterModule.class})
public interface ActivityComponent {
    IMainPresenter getMainPresenter();
    IBleServiceDisplayPresenter getBleServiceDisplayPresenter();
    IBleCharacteristicDisplayPresenter getBleCharacteristicDisplayPresenter();
}