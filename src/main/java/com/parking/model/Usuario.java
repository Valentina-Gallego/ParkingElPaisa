package com.parking.model;

public class Usuario {

    private int id;
    private int roleId;
    private String fullName;
    private String username;
    private String passwordHash;

    public Usuario() {
    }

    public Usuario(int id, int roleId, String fullName, String username) {
        this.id = id;
        this.roleId = roleId;
        this.fullName = fullName;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}