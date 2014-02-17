
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * 
 * @author Daniel Beraun
 * @version 1
 */
@SuppressWarnings("serial")
public class VectorFieldActionOn extends AbstractAction
{
  /**
   * A particle
   */
  ParticleDemo3D my_p;

  /**
   * A particle and its action
   */
  public VectorFieldActionOn(ParticleDemo3D p)
  {
    super("On");
    my_p = p;
  }

  @Override
  public void actionPerformed(ActionEvent the_event)
  {
    
      my_p.setVectorFieldOn();
  }
}
