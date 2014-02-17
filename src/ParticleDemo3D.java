import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author Daniel Beraun
 * @author Phil Adriaan
 * @version 1
 */
public class ParticleDemo3D
{
  /**
   * Physics updates per second (approximate).
   */
  private static final int UPDATE_RATE = 60;
  /**
   * Width of the extent in meters.
   */
  private static final float EXTENT_WIDTH = 10;

  float COEFFICIENT_OF_RESTITUTION = 0.9f;

  /**
   * Stifness of spring force
   */
  private static final float STIFFNESS = 3f;
  /**
   * Damping constant of the force
   */
  private static final float DAMPING = 0.1f;
  /**
   * Rest length of the spring
   */
  private static final float REST_LENGTH = 3f;

  /**
   * Array of universe limits
   */
  private final HalfSpace3D[] boundaries;
  /**
   * Array of the original particles
   */
  private final Particle3D[] original;
  /**
   *  Array of particles
   */
  private final Particle3D[] particles;

  /**
   * Turns gravity force on or off
   */
  boolean has_gravity;
  /**
   *  Turns wind force on or off
   */
  boolean has_wind;
  /**
   * Turns spring like force on or off
   */
  boolean has_spring;

  Transform3D t3D;

  MenuBar3D bar = new MenuBar3D(this);

  /**
   * X wind direction
   */
  float x_wind;
  /**
   * z wind direction
   */
  float z_wind;

  boolean my_vector;

  List<BranchGroup> vectors = new ArrayList<BranchGroup>();

  final BranchGroup scene = new BranchGroup();

  int counter = 0;

  public ParticleDemo3D()
  {
    scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    scene.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    scene.setCapability(BranchGroup.ALLOW_DETACH);
    scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

    boundaries =
        new HalfSpace3D[] {

        new HalfSpace3D(-EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, 1, 0, 0),
            new HalfSpace3D(-EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, 0, 1, 0),
            new HalfSpace3D(-EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, 0, 0, 1),
            new HalfSpace3D(EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -1, 0, 0),
            new HalfSpace3D(EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, 0, -1, 0),
            new HalfSpace3D(EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, 0, 0, -1)};
    original = new Particle3D[100];
    particles = new Particle3D[100];
    //storing the original particle to reset the simulation
    for (int i = 0; i < particles.length; i++)
    {
      original[i] =
          new Particle3D(1, (float) (Math.random() - .5) * EXTENT_WIDTH,
                         (float) (Math.random() - .5) * EXTENT_WIDTH,
                         (float) (Math.random() - .5) * EXTENT_WIDTH,
                         (float) (Math.random() - .5) * 10, (float) (Math.random() - .5) * 10,
                         (float) (Math.random() - .5) * 10, EXTENT_WIDTH * .008f, null);
      particles[i] =
          new Particle3D(original[i].mass, original[i].position.x, original[i].position.y,
                         original[i].position.z, original[i].velocity.x,
                         original[i].velocity.y, original[i].velocity.z, original[i].radius,
                         original[i].color);
    }
    final Random r = new Random();
    int w = r.nextInt(3);
    switch (w)
    {
      case 0:
        x_wind = -10;
        break;
      case 1:
        x_wind = 0;
        break;
      case 2:
        x_wind = 10;
      default:
        break;
    }
    w = r.nextInt(3);
    switch (w)
    {
      case 0:
        z_wind = -10;
        break;
      case 1:
        z_wind = 0;
        break;
      case 2:
        z_wind = 10;
        break;
      default:
        break;
    }
    reset();
  }

  public void setVectorFieldOn()
  {
    my_vector = true;
  }

  public void setVectorFieldOff()
  {
    my_vector = false;
  }

  public void setRestitution(final float rest)
  {
    COEFFICIENT_OF_RESTITUTION = rest;
  }

  /**
   * Reset forces and particles
   */
  public void reset()
  {
    has_gravity = false;
    has_wind = false;
    has_spring = false;
    for (int i = 0; i < particles.length; i++)
    {
      particles[i].mass = original[i].mass;
      particles[i].position.x = original[i].position.x;
      particles[i].position.y = original[i].position.y;
      particles[i].position.z = original[i].position.z;
      particles[i].velocity.x = original[i].velocity.x;
      particles[i].velocity.y = original[i].velocity.y;
      particles[i].velocity.z = original[i].velocity.z;
      particles[i].forceAccumulator.x = original[i].forceAccumulator.x;
      particles[i].forceAccumulator.y = original[i].forceAccumulator.y;
      particles[i].forceAccumulator.z = original[i].forceAccumulator.z;
    }
    bar.resetForces();
  }

  public void setGravity(final boolean g)
  {
    has_gravity = g;
  }

  public void setWind(final boolean w)
  {
    has_wind = w;
  }

  public void setSpring(final boolean s)
  {
    has_spring = s;
  }

  public static void main(final String[] args)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        new ParticleDemo3D().createAndShowGUI();
      }
    });
  }

  private void createAndShowGUI()
  {
    // Fix for background flickering on some platforms
    System.setProperty("sun.awt.noerasebackground", "true");

    final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    final Canvas3D canvas3D = new Canvas3D(config);
    final SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
    simpleU.getViewingPlatform().setNominalViewingTransform();
    // simpleU.getViewer().getView().setSceneAntialiasingEnable(true);

    // Add a scaling transform that resizes the virtual world to fit
    // within the standard view frustum.
    final TransformGroup worldScaleTG = new TransformGroup();
    worldScaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    t3D = new Transform3D();
    t3D.setScale(.9 / EXTENT_WIDTH);
    worldScaleTG.setTransform(t3D);
    final BranchGroup trueScene = new BranchGroup();
    trueScene.addChild(worldScaleTG);
    worldScaleTG.addChild(scene);

    final TransformGroup extentTransform = new TransformGroup();
    extentTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    extentTransform.addChild(createExtent());
    scene.addChild(extentTransform);

    for (Particle3D p : particles)
      scene.addChild(p.BG);
    simpleU.addBranchGraph(trueScene);

    final JFrame appFrame = new JFrame("Particle3D Simulator");
    appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    appFrame.add(canvas3D);
    appFrame.pack();
    if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
    {
      appFrame.setExtendedState(appFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    new Timer(1000 / UPDATE_RATE, new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        canvas3D.stopRenderer();
        tick();
        canvas3D.startRenderer();
      }
    }).start();

    canvas3D.addMouseMotionListener(new MouseMotionAdapter()
    {
      private MouseEvent lastDragEvent;

      public void mouseDragged(final MouseEvent e)
      {
        if (lastDragEvent != null)
        {
          final Transform3D newRotate = new Transform3D();
          newRotate.rotX(Math.toRadians(e.getY() - lastDragEvent.getY()) / 2);
          final Transform3D tmp = new Transform3D();
          tmp.rotY(Math.toRadians(e.getX() - lastDragEvent.getX()) / 2);
          newRotate.mul(tmp);
          newRotate.mul(t3D);
          t3D = newRotate;
          worldScaleTG.setTransform(t3D);
        }
        lastDragEvent = e;
      }

      public void mouseMoved(final MouseEvent e)
      {
        lastDragEvent = null;
      }
    });
    appFrame.setJMenuBar(bar);
    appFrame.setVisible(true);
  }

  /**
   * Creates a net force used to make a vector field
   * that represent the net force throughout the virtual
   * environment.
   * @param mass of particle
   * @param position of particle
   * @param velocity of particle
   * @return the net force
   */
  private Vector3f netForce(final float mass, final Vector3f position, final Vector3f velocity)
  {
    final Vector3f force = new Vector3f();
    if (has_wind)
    {
      final Random r = new Random();
      float p_x = x_wind;
      float p_z = z_wind;
      int w = r.nextInt(3);
      switch (w)
      {
        case 0:
          p_x--;
          break;
        case 1:
          p_x++;
          break;
        default:
          break;
      }
      w = r.nextInt(3);
      switch (w)
      {
        case 0:
          p_z--;
          break;
        case 1:
          p_z++;
          break;
        default:
          break;
      }
      force.x += p_x * mass;
      force.z += p_z * mass;
    }
    if (has_gravity)
    {
      force.y += -9.8f * mass;
    }
    if (has_spring)
    {
      if (position.x > EXTENT_WIDTH / 2 - REST_LENGTH ||
          position.x < -EXTENT_WIDTH / 2 + REST_LENGTH)
      {
        force.x += -STIFFNESS / mass * position.x - DAMPING / mass * velocity.x;
      }
      if (position.y > EXTENT_WIDTH / 2 - REST_LENGTH ||
          position.y < -EXTENT_WIDTH / 2 + REST_LENGTH)
      {
        force.y += -STIFFNESS / mass * position.y - DAMPING / mass * velocity.y;
      }
      if (position.z > EXTENT_WIDTH / 2 - REST_LENGTH ||
          position.z < -EXTENT_WIDTH / 2 + REST_LENGTH)
      {
        force.z += -STIFFNESS / mass * position.z - DAMPING / mass * velocity.z;
      }
    }

    return force;
  }

  private void tick()
  {
    final Random r = new Random();
    final int w1 = r.nextInt(Math.max((int) COEFFICIENT_OF_RESTITUTION, 4));
    switch (w1)
    {
      case 0:
        x_wind -= 1;
        break;
      case 1:
        x_wind += 1;
        break;
      case 2:
        z_wind -= 1;
        break;
      case 3:
        z_wind += 1;
        break;
      default:
        break;
    }
    x_wind = Math.max(x_wind, -10);
    x_wind = Math.min(x_wind, 10);
    z_wind = Math.max(z_wind, -10);
    z_wind = Math.min(z_wind, 10);
    for (Particle3D p : particles)
    {
      p.forceAccumulator = netForce(p.mass, p.position, p.velocity);
      p.updateState(1f / UPDATE_RATE);
      for (HalfSpace3D hs : boundaries)
        checkAndResolveCollision(hs, p);
      p.updateTransformGroup();
      // Clear the particle's force accumulator.
      p.forceAccumulator.x = p.forceAccumulator.y = p.forceAccumulator.z = 0;
    }

    if (counter >= 2)
    {
      if (!vectors.isEmpty())
      {
        for (BranchGroup i : vectors)
        {
          scene.removeChild(i);
        }
      }
      vectors.clear();
      if (my_vector)
      {
        for (int i = -5; i < 7; i += 2)
        {
          for (int j = -5; j < 7; j += 2)
          {
            for (int k = -5; k < 7; k += 2)
            {
              vectors.add(createVector(new Vector3f(i, j, k),
                                       netForce(1, new Vector3f(i, j, k),
                                                new Vector3f(0, 0, 0))));
              scene.addChild(vectors.get(vectors.size() - 1));
            }
          }
        }
      }
      counter = 0;
    }
    else
    {
      counter++;
    }
  }

  private void checkAndResolveCollision(final HalfSpace3D hs, final Particle3D p)
  {
    final float distance = hs.normal.dot(p.position) - hs.intercept;
    if (distance < 0)
    {
      // Use Torricelli's equation to approximate the particle's
      // velocity (v_i) at the time it contacts the halfspace.
      // v_f^2 = v_i^2 + 2 * acceleration * distance

      // Final velocity of the particle in the direction of the halfspace normal
      final float v_f = hs.normal.dot(p.velocity);
      // Velocity of the particle in the direction of the halfspace normal at
      // the
      // time of contact, squared
      float v_i_squared = v_f * v_f - 2 * p.forceAccumulator.dot(hs.normal) * distance;
      // If v_i_squared is less than zero, then the quantities involved are so
      // small
      // that numerical inaccuracy has produced an impossible result. The
      // velocity
      // at the time of contact should therefore be zero.
      if (v_i_squared < 0)
      {
        v_i_squared = 0;
      }
      // Remove the incorrect velocity acquired after the contact and add the
      // flipped
      // correct velocity.
      p.velocity.scaleAdd(-v_f + COEFFICIENT_OF_RESTITUTION * (float) Math.sqrt(v_i_squared),
                          hs.normal, p.velocity);

      // Old code for adjusting the velocity.
      // p.velocity.scaleAdd(-(1 + COEFFICIENT_OF_RESTITUTION) *
      // hs.normal.dot(p.velocity), hs.normal, p.velocity);

      p.position.scaleAdd(-distance, hs.normal, p.position);
    }
  }

  private BranchGroup createVector(final Vector3f position, final Vector3f force)
  {
    final float[] coordinates =
        {position.x, position.y, position.z, position.x + force.x * 0.1f,
            position.y + force.y * 0.1f, position.z + force.z * 0.1f};
    final LineStripArray geometry =
        new LineStripArray(2, GeometryArray.COORDINATES, new int[] {2});

    geometry.setCoordinates(0, coordinates);
    final Shape3D shape = new Shape3D(geometry);
    final Appearance appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(0.25f, 0.25f, 0.25f,
                                                            ColoringAttributes.FASTEST));
    shape.setAppearance(appearance);
    final BranchGroup b = new BranchGroup();
    b.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    b.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    b.setCapability(BranchGroup.ALLOW_DETACH);
    b.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    b.addChild(shape);
    return b;
  }

  /**
   * The extent representing the universe
   * @return a cube made with lines
   */
  private Node createExtent()
  {
    final float[] coordinates =
        {-EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2,
            -EXTENT_WIDTH / 2, -EXTENT_WIDTH / 2, EXTENT_WIDTH / 2, EXTENT_WIDTH / 2};
    final LineStripArray geometry =
        new LineStripArray(16, GeometryArray.COORDINATES, new int[] {16});

    geometry.setCoordinates(0, coordinates);
    final Shape3D shape = new Shape3D(geometry);
    final Appearance appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(1f, 1f, 1f,
                                                            ColoringAttributes.FASTEST));
    shape.setAppearance(appearance);
    return shape;
  }
}
