package com.project.s1s1s1.syncdemo;

class User {
    private int id;
    private String name;
    private int sync_status;

    public User() {
    }

    public User(String name, int sync_status) {
        this.name = name;
        this.sync_status = sync_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
