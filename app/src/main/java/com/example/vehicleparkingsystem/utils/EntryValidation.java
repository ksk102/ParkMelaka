package com.example.vehicleparkingsystem.utils;

import android.widget.EditText;

public class EntryValidation {
    public boolean validateUserName(EditText userEdit){

        String name = userEdit.getText().toString().trim();

        if(name.equals("") || name.isEmpty()){
            userEdit.requestFocus();
            userEdit.setError("Name is required");

            return false;
        }
        else if(!name.matches("[a-zA-Z ]+")){
            userEdit.requestFocus();
            userEdit.setError("A name must be characters only");

            return false;
        }

        return true;
    }

    public boolean validateEmail(EditText emailEdit){
        String email = emailEdit.getText().toString().trim();

        if(email.equals("") || email.isEmpty()){
            emailEdit.requestFocus();
            emailEdit.setError("Email is required");

            return false;
        }
        else if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            emailEdit.requestFocus();
            emailEdit.setError("Your email is not valid");

            return false;
        }

        return true;
    }

    public boolean validatePassword(EditText passwordEdit){
        String password = passwordEdit.getText().toString();

        if(password.equals("") || password.isEmpty()){
            passwordEdit.requestFocus();
            passwordEdit.setError("Password is required");

            return false;
        }
        else if(!password.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
            passwordEdit.requestFocus();
            passwordEdit.setError("UpperCase, LowerCase, Number/SpecialChar and min 8 Chars");

            return false;
        }

        return true;
    }

    public boolean validateConfirmPassword(EditText passwordEdit, EditText confirmPasswordEdit){
        String password = passwordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();

        if(!confirmPassword.equals(password)){
            confirmPasswordEdit.requestFocus();
            confirmPasswordEdit.setError("Password and Confirm Password not match");

            return false;
        }

        return true;
    }

    public boolean validateCarPlateNumber(EditText carPlateEdit){
        String carPlate = carPlateEdit.getText().toString().trim();

        if(carPlate.equals("") || carPlate.isEmpty()){
            carPlateEdit.requestFocus();
            carPlateEdit.setError("Car Plate Number is required");

            return  false;
        }
        else if(!carPlate.matches("^[A-z]{3}( )?[0-9]{1,4}( )?[A-z]?$")){
            carPlateEdit.requestFocus();
            carPlateEdit.setError("Malaysia Car Plate Number only");

            return false;
        }

        return true;
    }
}
