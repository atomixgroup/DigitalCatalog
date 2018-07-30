package ir.codetower.samanshiri.Models;

/**
 * Created by Mr-R00t on 9/10/2017.
 */

public class ProductChecker {
    private boolean primary;
    private boolean p1;
    private boolean p2;
    private boolean p3;
    private boolean p4;
    private boolean p5;
    private boolean p6;

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isP1() {
        return p1;
    }

    public void setP1(boolean p1) {
        this.p1 = p1;
    }

    public boolean isP2() {
        return p2;
    }

    public void setP2(boolean p2) {
        this.p2 = p2;
    }

    public boolean isP3() {
        return p3;
    }

    public void setP3(boolean p3) {
        this.p3 = p3;
    }

    public boolean isP4() {
        return p4;
    }

    public void setP4(boolean p4) {
        this.p4 = p4;
    }

    public boolean isP5() {
        return p5;
    }

    public void setP5(boolean p5) {
        this.p5 = p5;
    }

    public boolean isP6() {
        return p6;
    }

    public void setP6(boolean p6) {
        this.p6 = p6;
    }
    public boolean checkProduct(){
        if(!isPrimary()) return false;
        if(!isP1()) return false;
        if(!isP2()) return false;
        if(!isP3()) return false;
        if(!isP4()) return false;
        if(!isP5()) return false;
        if(!isP6()) return false;
        return true;
    }
}
