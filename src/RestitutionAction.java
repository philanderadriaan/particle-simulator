import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * 
 * @author Phil Adriaan
 * @version 1
 */
public class RestitutionAction extends AbstractAction
{
  /**
   *  A particle
   */
  ParticleDemo3D my_p;

  float my_rest;

  /**
   * A particle and its action
   */
  public RestitutionAction(ParticleDemo3D p, float rest)
  {
    super("" + rest);
    my_p = p;
    my_rest = rest;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    my_p.setRestitution(my_rest);
  }
}
