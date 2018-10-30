/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package weka.gui.stpm;

/**
 *
 * @author Simone
 */
public class AssociatedParameter {
    String name;
    Integer value;
    String type;

    public AssociatedParameter(String p) {
        name = p;
    }
    
    public AssociatedParameter(String p,String t, Integer v) {
        name = p;
        type = t;
        value = v;
    }

    public String toString() {
        return name;
    }
}
