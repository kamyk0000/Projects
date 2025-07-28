package sample;

public class Sheet {
    private String nazwa, rasa, klasa;
    private int poziom, pancerz, szybkosc, inicjatywa, percepcja, bieglosc,
    sila, wytrzymalosc, zrecznosc, inteligencja, wiedza, charyzma, mocZaklec, klasaZaklec;
    private String atrybuty;
    private String created, edited;
    private String bron1,bron2,bron3;
    private String other, boxes;


    public Sheet(String nazwa, String rasa, String klasa, int poziom, int pancerz, int szybkosc, int inicjatywa, int percepcja,
                 int bieglosc, int sila, int wytrzymalosc, int zrecznosc, int inteligencja, int wiedza, int charyzma, int mocZaklec,
                 int klasaZaklec, String created, String edited, String bron1, String bron2, String bron3, String other, String boxes) {
        this.nazwa = nazwa;
        this.rasa = rasa;
        this.klasa = klasa;
        this.poziom = poziom;
        this.pancerz = pancerz;
        this.szybkosc = szybkosc;
        this.inicjatywa = inicjatywa;
        this.percepcja = percepcja;
        this.bieglosc = bieglosc;
        this.sila = sila;
        this.wytrzymalosc = wytrzymalosc;
        this.zrecznosc = zrecznosc;
        this.inteligencja = inteligencja;
        this.wiedza = wiedza;
        this.charyzma = charyzma;
        this.mocZaklec = mocZaklec;
        this.klasaZaklec = klasaZaklec;
        this.created = created;
        this.edited = edited;
        this.bron1 = bron1;
        this.bron2 = bron2;
        this.bron3 = bron3;
        this.other = other;
        this.boxes = boxes;
        atrybuty=sila+", "+wytrzymalosc+", "+zrecznosc+", "+inteligencja+", "+wiedza+", "+charyzma;
    }

    public String getBron1() {
        return bron1;
    }

    public void setBron1(String bron1) {
        this.bron1 = bron1;
    }

    public String getBron2() {
        return bron2;
    }

    public void setBron2(String bron2) {
        this.bron2 = bron2;
    }

    public String getBron3() {
        return bron3;
    }

    public void setBron3(String bron3) {
        this.bron3 = bron3;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public String getAtrybuty() {
        return atrybuty;
    }

    public void setAtrybuty(String atrybuty) {
        this.atrybuty = atrybuty;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getRasa() {
        return rasa;
    }

    public void setRasa(String rasa) {
        this.rasa = rasa;
    }

    public String getKlasa() {
        return klasa;
    }

    public void setKlasa(String klasa) {
        this.klasa = klasa;
    }

    public int getPoziom() {
        return poziom;
    }

    public void setPoziom(int poziom) {
        this.poziom = poziom;
    }

    public int getPancerz() {
        return pancerz;
    }

    public void setPancerz(int pancerz) {
        this.pancerz = pancerz;
    }

    public int getSzybkosc() {
        return szybkosc;
    }

    public void setSzybkosc(int szybkosc) {
        this.szybkosc = szybkosc;
    }

    public int getInicjatywa() {
        return inicjatywa;
    }

    public void setInicjatywa(int inicjatywa) {
        this.inicjatywa = inicjatywa;
    }

    public int getPercepcja() {
        return percepcja;
    }

    public void setPercepcja(int percepcja) {
        this.percepcja = percepcja;
    }

    public int getBieglosc() {
        return bieglosc;
    }

    public void setBieglosc(int bieglosc) {
        this.bieglosc = bieglosc;
    }

    public int getSila() {
        return sila;
    }

    public void setSila(int sila) {
        this.sila = sila;
    }

    public int getWytrzymalosc() {
        return wytrzymalosc;
    }

    public void setWytrzymalosc(int wytrzymalosc) {
        this.wytrzymalosc = wytrzymalosc;
    }

    public int getZrecznosc() {
        return zrecznosc;
    }

    public void setZrecznosc(int zrecznosc) {
        this.zrecznosc = zrecznosc;
    }

    public int getInteligencja() {
        return inteligencja;
    }

    public void setInteligencja(int inteligencja) {
        this.inteligencja = inteligencja;
    }

    public int getWiedza() {
        return wiedza;
    }

    public void setWiedza(int wiedza) {
        this.wiedza = wiedza;
    }

    public int getCharyzma() {
        return charyzma;
    }

    public void setCharyzma(int charyzma) {
        this.charyzma = charyzma;
    }

    public int getMocZaklec() {
        return mocZaklec;
    }

    public void setMocZaklec(int mocZaklec) {
        this.mocZaklec = mocZaklec;
    }

    public int getKlasaZaklec() {
        return klasaZaklec;
    }

    public void setKlasaZaklec(int klasaZaklec) {
        this.klasaZaklec = klasaZaklec;
    }

    public String getBoxes() {
        return boxes;
    }

    public void setBoxes(String boxes) {
        this.boxes = boxes;
    }

    @Override
    public String toString() {
        return nazwa+"\t"
                +rasa+"\t"
                +klasa+"\t"
                +poziom+"\t"
                +pancerz+"\t"
                +szybkosc+"\t"
                +inicjatywa+"\t"
                +percepcja+"\t"
                +bieglosc+"\t"
                +sila+"\t"
                +wytrzymalosc+"\t"
                +zrecznosc+"\t"
                +inteligencja+"\t"
                +wiedza+"\t"
                +charyzma+"\t"
                +mocZaklec+"\t"
                +klasaZaklec+"\t"
                +created+"\t"
                +edited+"\t"
                +bron1 +"\t"
                +bron2+"\t"
                +bron3+"\t"
                +other+"\t"
                +boxes
                ;
    }
}

/*

                +poziom+"\t"
                +pancerz+"\t"
                +szybkosc+"\t"
                +inicjatywa+"\t"
                +percepcja+"\t"
                +bieglosc+"\t"
                +atrybuty
 */
