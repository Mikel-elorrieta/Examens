package com.example.sqlariketa;

import java.io.Serializable;

public class ProgramazioLengoaia  implements Serializable {


    private String ID;
   private String IZENA;
    private String DESKRIBAPENA;
    private boolean SoftwareLibrea;


    public ProgramazioLengoaia( String ID , String IZENA, String DESKRIBAPENA, boolean SoftwareLibrea) {
        this.ID = ID;
        this.IZENA = IZENA;
        this.DESKRIBAPENA = DESKRIBAPENA;
        this.SoftwareLibrea = SoftwareLibrea;

    }

    public ProgramazioLengoaia(String IZENA, String DESKRIBAPENA, boolean SoftwareLibrea) {
        this.IZENA = IZENA;
        this.DESKRIBAPENA = DESKRIBAPENA;
        this.SoftwareLibrea = SoftwareLibrea;
    }
    public boolean isSoftwareLibrea() {
        return SoftwareLibrea;
    }
    public void setSoftwareLibrea(boolean SoftwareLibrea) {
        this.SoftwareLibrea = SoftwareLibrea;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIzena() {
        return IZENA;
    }
    public String getDeskribapena() {
        return DESKRIBAPENA;
    }
    public void setIzena(String IZENA) {
        this.IZENA = IZENA;
    }
    public void setDeskribapena(String DESKRIBAPENA) {
        this.DESKRIBAPENA = DESKRIBAPENA;
    }
    @Override
    public String toString() {
        return "ProgramazioLengoaia{" +
                "ID='" + ID + '\'' +
                ", IZENA='" + IZENA + '\'' +
                ", DESKRIBAPENA='" + DESKRIBAPENA + '\'' +
                ", SoftwareLibrea=" + SoftwareLibrea +
                '}';
    }


}
