package com.example.lockmeow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> buttonClicked = new MutableLiveData<>();

    public void setButtonClicked(int buttonId) {
        buttonClicked.setValue(buttonId);
    }

    public LiveData<Integer> getButtonClicked() {
        return buttonClicked;
    }
}
