import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * 
 * @author Phil Adriaan
 * @version 1
 */
public class ResetAction extends AbstractAction
{
  /**
   * A particle
   */
  ParticleDemo3D my_p;

  /**
   * A particle and its action
   */
  public ResetAction(ParticleDemo3D p)
  {
    super("Reset");
    my_p = p;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    my_p.reset();
  }
}
