import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

/**
 * 
 * @author Phil Adriaan
 * @version 1
 */
public class SpringAction extends AbstractAction
{
  /**
   * A particle 
   */
  ParticleDemo3D my_p;

  /**
   * A particle and its action
   */
  public SpringAction(ParticleDemo3D p)
  {
    super("Spring");
    my_p = p;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    JCheckBoxMenuItem cb; //= new JCheckBoxMenuItem();
    cb = (JCheckBoxMenuItem)the_event.getSource();
    my_p.setSpring(cb.isSelected());
  }
}