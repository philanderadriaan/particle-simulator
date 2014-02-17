import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

/**
 * 
 * @author Phil Adriaan
 * @version 1
 */
public class GravityAction extends AbstractAction
{
  /**
   * A particle
   */
  ParticleDemo3D my_p;

  /**
   * Particle and name
   */
  public GravityAction(ParticleDemo3D p)
  {
    super("Gravity");
    my_p = p;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    JCheckBoxMenuItem cb; //= new JCheckBoxMenuItem();
    cb = (JCheckBoxMenuItem)the_event.getSource();
    my_p.setGravity(cb.isSelected());
  }
}