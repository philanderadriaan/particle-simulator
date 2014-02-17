import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
/**
 * @author Daniel Beraun
 * @author Phil Adriaan
 * @version 1
 */
public class MenuBar3D extends JMenuBar
{
  ParticleDemo3D my_p;
  List<JCheckBoxMenuItem> forces = new ArrayList<JCheckBoxMenuItem>();

  /**
   * Creates a Menu bar
   * @param p particle
   */
  public MenuBar3D(ParticleDemo3D p)
  {
    super();
    my_p = p;

    forces.add(new JCheckBoxMenuItem(new GravityAction(my_p)));
    forces.add(new JCheckBoxMenuItem(new WindAction(my_p)));
    forces.add(new JCheckBoxMenuItem(new SpringAction(my_p)));
    add(forceMenu());
    add(coefficientOfRestitutionMenu());
    add(vectorFieldMenu());
    add(adjustMenu());
  }

  private JMenu forceMenu()
  {
    final JMenu force_menu = new JMenu(" Force ");
    for (JCheckBoxMenuItem i : forces)
    {
      force_menu.add(i);
    }
    return force_menu;
  }

  private JMenu coefficientOfRestitutionMenu()
  {
    final JMenu restitution_menu = new JMenu(" Coefficient of Restitution ");
    ButtonGroup group = new ButtonGroup();
    for (int i = 0; i <= 20; i++)
    {
      float cor = ((float) i) / 10;
      JRadioButtonMenuItem rest_button =
          new JRadioButtonMenuItem(new RestitutionAction(my_p, cor));
      if (cor == my_p.COEFFICIENT_OF_RESTITUTION)
      {
        rest_button.setSelected(true);
      }
      restitution_menu.add(rest_button);
      group.add(rest_button);
    }
    return restitution_menu;
  }

  private JMenu vectorFieldMenu()
  {
    final JMenu vector_field_menu = new JMenu(" Vector Field ");
    final ButtonGroup vector_field_group = new ButtonGroup();
    final JRadioButtonMenuItem on_button =
        new JRadioButtonMenuItem(new VectorFieldActionOn(my_p));
    vector_field_menu.add(on_button);
    vector_field_group.add(on_button);
    final JRadioButtonMenuItem off_button =
        new JRadioButtonMenuItem(new VectorFieldActionOff(my_p));
    vector_field_menu.add(off_button);
    vector_field_group.add(off_button);
    off_button.setSelected(true);
    return vector_field_menu;
  }

  private JMenu adjustMenu()
  {
    final JMenu restitution_menu = new JMenu(" Adjust ");
    restitution_menu.add(new JMenuItem(new ResetAction(my_p)));
    return restitution_menu;
  }

  public void resetForces()
  {
    for (JCheckBoxMenuItem i : forces)
    {
      i.setSelected(false);
    }
  }

}
