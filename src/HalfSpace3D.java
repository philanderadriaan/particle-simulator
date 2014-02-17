import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 * @author Daniel Beraun
 * @author Phil Adriaan
 * @version 1
 */
public class HalfSpace3D
{
  /**
   * Position in 3D space.
   */
  public Vector3f position;
  /**
   * Normal in 3D space.
   */
  public Vector3f normal;
  /**
   * Right-hand side of the plane equation A * x + B * y = C
   */
  public float intercept;

  /**
   * A 3D halfspace contructor
   * @param positionX
   * @param positionY
   * @param positionZ
   * @param normalX
   * @param normalY
   * @param normalZ
   */
  public HalfSpace3D(float positionX, float positionY, float positionZ, float normalX,
                     float normalY, float normalZ)
  {
    position = new Vector3f(positionX, positionY, positionZ);
    normal = new Vector3f(normalX, normalY, normalZ);
    normal.normalize();
    intercept = normal.dot(position);
  }

  public HalfSpace3D(Tuple3f position, Tuple3f the_normal)
  {
    this(position.x, position.y, position.z, the_normal.x, the_normal.y, the_normal.z);
  }
}