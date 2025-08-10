package com.softgenix.Service;

import com.softgenix.Dao.UserDAO;
import com.softgenix.Model.User;

public class AuthService {


    public static User iniciarSesion(String email, String password) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return null;
        }

        return UserDAO.obtenerUsuarioPorCredenciales(email, password);
    }
}
