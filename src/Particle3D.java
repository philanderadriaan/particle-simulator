import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

/**
 * @author Daniel Beraun
 * @author Phil Adriaan
 * @version 1
 */
 
public class Particle3D
{
  /**
   * Mass of particle.
   */
  public float mass;
  /**
   * Position of particle in 3D space.
   */
  public Vector3f position;
  /**
   * Velocity of particle in 3D space.
   */
  public Vector3f velocity;
  /**
   * Force accumulator vector.
   */
  public Vector3f forceAccumulator;
  /**
   * Branch group for a particle.
   */
  public BranchGroup BG;
  /**
   * Transform group for a particle.
   */
  private TransformGroup TG;
  /**
   * Transform properties for a particle.
   */
  private Transform3D T3D;
  /**
   * Radius of particle
   */
  float radius;
  /**
   * Color of particle.
   */
  Color3f color;
  /**
   * Particle can't exceed this velocity.
   */
  float maximum_velocity = 100; 
  /**
   * Creates a particle.
   * 
   * @param the_mass Mass of particle.
   * @param positionX X position of particle.
   * @param positionY Y position of particle.
   * @param positionZ Z position of particle.
   * @param velocityX Velocity of particle in X plane.
   * @param velocityY Velocity of particle in Y plane.
   * @param velocityZ Velocity of particle in Z plane.
   * @param radius Radius of particle.
   * @param color Color of particle.
   */
  public Particle3D(float the_mass, float positionX, float positionY, float positionZ,
                    float velocityX, float velocityY, float velocityZ, float radius,
                    final Color3f color)
  {
    if (the_mass <= 0)
    {
      throw new IllegalArgumentException();
    }
    this.radius = radius;
    this.mass = the_mass;
    position = new Vector3f(positionX, positionY, positionZ);
    velocity = new Vector3f(velocityX, velocityY, velocityZ);
    forceAccumulator = new Vector3f();
    BG = new BranchGroup();
    BG.setCapability(BranchGroup.ALLOW_DETACH);
    TG = new TransformGroup();
    TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    TG.addChild(createShape(radius, color));
    BG.addChild(TG);
    T3D = new Transform3D();
    updateTransformGroup();
  }

  /**
   * Creates a particle.
   * 
   * @param the_mass Mass of particle.
   * @param position Position of particle in 3D space.
   * @param velocity Velocity of particle in 3D space.
   * @param radius Radius of particle.
   * @param color Color of particle.
   */
  public Particle3D(float the_mass, Tuple3f position, Tuple3f velocity, float radius,
                    final Color3f color)
  {
    this(the_mass, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z,
         radius, color);
  }

  /**
   * Update the state of the particle.
   * 
   * @param duration Time elapsed.
   */
  public void updateState(float duration)
  {
    // The force accumulator vector (net force) now becomes
    // the acceleration vector.
    forceAccumulator.scale(1 / mass);
    position.scaleAdd(duration, velocity, position);
    position.scaleAdd(duration * duration / 2, forceAccumulator, position);
    velocity.scaleAdd(duration, forceAccumulator, velocity);
    velocity.x = Math.min(velocity.x, maximum_velocity);
    velocity.y = Math.min(velocity.y, maximum_velocity);
    velocity.z = Math.min(velocity.z, maximum_velocity);
  }

  /**
   * Update the particle movement to the transform group.
   */
  public void updateTransformGroup()
  {
    T3D.setTranslation(new Vector3f(position.x, position.y, position.z));
    TG.setTransform(T3D);
  }

  /**
   * @param radius Radius of the particle.
   * @param color Color of the particle.
   * @return Sphere representation of the particle.
   */
  private Node createShape(float radius, Color3f color)
  {
    if (color == null)
      color =
          new Color3f(Color.getHSBColor((float) Math.random(), (float) Math.random(),
                                        (float) Math.random()));
    this.color = color;
    Appearance appearance = new Appearance();
    appearance
        .setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));
    return new Sphere(radius, 0, 8, appearance);
  }
}