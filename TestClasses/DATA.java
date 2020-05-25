import java.math.BigInteger;
import java.util.function.BiFunction;

import CipollasAlgorithm.Point;

public class DATA extends DataClass1 implements DataClass2 {
        BigInteger x;
        BigInteger y;
        boolean b;

        DATA(BigInteger x, BigInteger y, boolean b) {
            this.x = x;
            this.y = y;
            this.b = b;
            
            x = a().b().c().d();
        }

        @Override
        public String toString() {
            return String.format("(%s, %s, %s)", this.x, this.y, this.b);
            
            BiFunction<Point, Point, Point> mul = (Point aa, Point bb) -> new Point(
                    aa.x.multiply(bb.x).add(aa.y.multiply(bb.y).multiply(finalOmega)).mod(p),
                    aa.x.multiply(bb.y).add(bb.x.multiply(aa.y)).mod(p)
            );
        }
        
        public void setX(BigInteger x)
        {
        	this.x = x;
        }
        
        public BigInteger getX(BigInteger x)
        {
        	return this.x;
        }
        
        public void method1(int a)
    	{
        	a++;
        	super.method1(a, a.toString());
    	}
        
        public void method1(int a, String b)
    	{
        	a++;
    		super.method1(a, b);
    	}
        
        public void method3(int a, String b)
        {
        	
        }

    	
    	public void method4()
    	{
    		
    	}
}
