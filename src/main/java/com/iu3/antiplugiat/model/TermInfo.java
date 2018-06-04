package com.iu3.antiplugiat.model;

/**
 *
 * @author Andalon
 */
public class TermInfo implements Comparable{

    private int docID;
    private int pos;

    public TermInfo(int docID, int pos) {
        this.docID = docID;
        this.pos = pos;
    }

    public int getDocID() {
        return docID;
    }

    public int getPos() {
        return pos;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.docID;
        hash = 97 * hash + this.pos;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TermInfo other = (TermInfo) obj;
        if (this.docID != other.docID) {
            return false;
        }
        if (this.pos != other.pos) {
            return false;
        }
        return true;
    }
    
    public TermInfo getBigger(){
        
        return new TermInfo(docID,pos+1);
        
    }

    @Override
    public int compareTo(Object obj) {
        
        final TermInfo other = (TermInfo) obj;
        if (obj.equals(this)){
            return 0;
        }
        if (docID<other.getDocID())
            return -1;
        if (docID>other.getDocID())
            return 1;
        if (docID==other.getDocID()){
            if (pos>other.getPos())
                return 1;
            else
                return -1;
            
        }
        return 0;
    }

}
