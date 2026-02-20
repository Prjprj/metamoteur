package agent;

/*
 * This file is part of "Méta-moteur".
 *
 * (c) Méta-moteur 2005-2006. All Rights Reserved.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * --LICENSE NOTICE--
 */

/**
 * Classe de configuration immuable du MetaMoteur.
 * Remplace les champs statiques mutables de la classe {@link Agent}.
 * <p>
 * Utilisation via le pattern Builder :
 * <pre>
 *   AppConfig config = AppConfig.builder()
 *       .portServeur(8080)
 *       .nbThread(5)
 *       .build();
 * </pre>
 *
 * @version 2.0
 */
public final class AppConfig {

    private final String hostBDD;
    private final String userBDD;
    private final String passBDD;
    private final String baseBDD;
    private final String tableBDD;
    private final String typeBDD;
    private final int sortie;
    private final int debug;
    private final int promLocale;
    private final int coefPonderation;
    private final double coefSim;
    private final String fichierLog;
    private final int portServeur;
    private final int nbThread;
    private final String pageIndex;
    private final String path;
    private final String[] moteurs;
    private final String[] contacts;

    private AppConfig(Builder builder) {
        this.hostBDD        = builder.hostBDD;
        this.userBDD        = builder.userBDD;
        this.passBDD        = builder.passBDD;
        this.baseBDD        = builder.baseBDD;
        this.tableBDD       = builder.tableBDD;
        this.typeBDD        = builder.typeBDD;
        this.sortie         = builder.sortie;
        this.debug          = builder.debug;
        this.promLocale     = builder.promLocale;
        this.coefPonderation = builder.coefPonderation;
        this.coefSim        = builder.coefSim;
        this.fichierLog     = builder.fichierLog;
        this.portServeur    = builder.portServeur;
        this.nbThread       = builder.nbThread;
        this.pageIndex      = builder.pageIndex;
        this.path           = builder.path;
        this.moteurs        = builder.moteurs.clone();
        this.contacts       = builder.contacts.clone();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getHostBDD()         { return hostBDD; }
    public String getUserBDD()         { return userBDD; }
    public String getPassBDD()         { return passBDD; }
    public String getBaseBDD()         { return baseBDD; }
    public String getTableBDD()        { return tableBDD; }
    public String getTypeBDD()         { return typeBDD; }
    public int    getSortie()          { return sortie; }
    public int    getDebug()           { return debug; }
    public int    getPromLocale()      { return promLocale; }
    public int    getCoefPonderation() { return coefPonderation; }
    public double getCoefSim()         { return coefSim; }
    public String getFichierLog()      { return fichierLog; }
    public int    getPortServeur()     { return portServeur; }
    public int    getNbThread()        { return nbThread; }
    public String getPageIndex()       { return pageIndex; }
    public String getPath()            { return path; }

    /** @return copie défensive du tableau des moteurs */
    public String[] getMoteurs()  { return moteurs.clone(); }

    /** @return copie défensive du tableau des contacts */
    public String[] getContacts() { return contacts.clone(); }

    // -------------------------------------------------------------------------
    // Builder factory
    // -------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder pour construire un {@link AppConfig} immuable.
     * Les valeurs par défaut correspondent aux valeurs originales de {@link Agent}.
     */
    public static final class Builder {

        private String   hostBDD        = "";
        private String   userBDD        = "";
        private String   passBDD        = "";
        private String   baseBDD        = "";
        private String   tableBDD       = "";
        private String   typeBDD        = "";
        private int      sortie         = 3;
        private int      debug          = 3;
        private int      promLocale     = 2;
        private int      coefPonderation = 1;
        private double   coefSim        = 0.7;
        private String   fichierLog     = "metaMoteur.log";
        private int      portServeur    = 8080;
        private int      nbThread       = 5;
        private String   pageIndex      = "index.html";
        private String   path           = "/www/";
        private String[] moteurs        = new String[]{""};
        private String[] contacts       = new String[]{""};

        private Builder() {}

        public Builder hostBDD(String v)         { this.hostBDD = v;         return this; }
        public Builder userBDD(String v)         { this.userBDD = v;         return this; }
        public Builder passBDD(String v)         { this.passBDD = v;         return this; }
        public Builder baseBDD(String v)         { this.baseBDD = v;         return this; }
        public Builder tableBDD(String v)        { this.tableBDD = v;        return this; }
        public Builder typeBDD(String v)         { this.typeBDD = v;         return this; }
        public Builder sortie(int v)             { this.sortie = v;          return this; }
        public Builder debug(int v)              { this.debug = v;           return this; }
        public Builder promLocale(int v)         { this.promLocale = v;      return this; }
        public Builder coefPonderation(int v)    { this.coefPonderation = v; return this; }
        public Builder coefSim(double v)         { this.coefSim = v;         return this; }
        public Builder fichierLog(String v)      { this.fichierLog = v;      return this; }
        public Builder portServeur(int v)        { this.portServeur = v;     return this; }
        public Builder nbThread(int v)           { this.nbThread = v;        return this; }
        public Builder pageIndex(String v)       { this.pageIndex = v;       return this; }
        public Builder path(String v)            { this.path = v;            return this; }
        public Builder moteurs(String[] v)       { this.moteurs = v.clone(); return this; }
        public Builder contacts(String[] v)      { this.contacts = v.clone();return this; }

        public AppConfig build() {
            return new AppConfig(this);
        }
    }
}
