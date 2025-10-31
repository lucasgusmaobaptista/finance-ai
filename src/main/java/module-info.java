module me.lucasgusmao.financeai {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jakarta.persistence;
    requires org.kordamp.ikonli.materialdesign2;
    requires static lombok;
    requires spring.context;
    requires spring.data.jpa;
    requires spring.security.crypto;
    requires spring.web;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.beans;
    requires spring.core;

    requires org.hibernate.orm.core;
    requires java.sql;
    requires static java.instrument;

    opens me.lucasgusmao.financeai to javafx.fxml, spring.core, spring.beans, spring.context;
    opens me.lucasgusmao.financeai.config to spring.core, spring.beans, spring.context;
    opens me.lucasgusmao.financeai.model;
    opens me.lucasgusmao.financeai.service to spring.core, spring.beans, spring.context;
    opens me.lucasgusmao.financeai.repository to spring.core, spring.beans, spring.context;
    opens me.lucasgusmao.financeai.controller to javafx.fxml, spring.core, spring.beans, spring.context;
    opens me.lucasgusmao.financeai.view to javafx.fxml;

    exports me.lucasgusmao.financeai.view;
    exports me.lucasgusmao.financeai.controller;
    exports me.lucasgusmao.financeai.config;
    exports me.lucasgusmao.financeai;
    exports me.lucasgusmao.financeai.model;
    exports me.lucasgusmao.financeai.service;
    exports me.lucasgusmao.financeai.repository;
}