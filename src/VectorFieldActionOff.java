
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * 
 * @author Daniel Beraun
 * @version 1
 */
@SuppressWarnings("serial")
public class VectorFieldActionOff extends AbstractAction
{
  /**
   * A particle 
   */
  ParticleDemo3D my_p;

  /**
   * A particle and its action
   */
  public VectorFieldActionOff(ParticleDemo3D p)
  {
    super("Off");
    my_p = p;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    
      my_p.setVectorFieldOff();
  }
}
