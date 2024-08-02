//Symbolic placeholder to satisfy the compiler 
class RUNTIME_CONTEXT{

}

//Enum for the operators
enum OPERATOR{
	PLUS,
	MINUS,
	MUL,
	DIV
}

//An abstract class for our expression and an abstract method to evaluate --> Expression : 2 + 3, 5 - 2 + 6, ...
abstract class Exp{
	public abstract double Evaluate(RUNTIME_CONTEXT cont);
}


//Class for holding the value in an expression --> if 2 + 3 is an expression 2 & 3 are numeric constants
class NumericConstant extends Exp{
	private double value;
	
	public NumericConstant(double value){
		this.value = value;
	}
	
	@Override
	public double Evaluate(RUNTIME_CONTEXT cont){
		return value;
	}
}

//Class for binary expression evaluation --> 6 * 8 = 48 , 7 + 3 = 10, 22 - 14 = 8, 4 / 2 = 2
class BinaryExp extends Exp{
	private Exp ex1, ex2;
	private OPERATOR op;

	public BinaryExp(Exp ex1, OPERATOR op, Exp ex2){
		this.ex1 = ex1;
		this.op = op;
		this.ex2 = ex2;
	}

	@Override
	public double Evaluate(RUNTIME_CONTEXT cont){
		switch(op){
			case PLUS:
				return ex1.Evaluate(cont) + ex2.Evaluate(cont);
			case MINUS:
				return ex1.Evaluate(cont) - ex2.Evaluate(cont);
			case MUL:
				return ex1.Evaluate(cont) * ex2.Evaluate(cont);
			case DIV:
				return ex1.Evaluate(cont) / ex2.Evaluate(cont);
			
		}
		return Double.NaN;
	}
}


//Class for unary expression evaluation --> +6 = 6, -6 = -6
class UnaryExp extends Exp{
	private Exp ex1;
	private OPERATOR op;
	
	public UnaryExp(Exp ex1, OPERATOR op){
		this.ex1 = ex1;
		this.op = op;
	}
	
	@Override
	public double Evaluate(RUNTIME_CONTEXT cont){
		switch(op){
				case PLUS:
					return ex1.Evaluate(cont);
				case MINUS:
					return -ex1.Evaluate(cont);
			
			}
			return Double.NaN;
	}
}

//Main class & method
public class EntryPoint {
	public static void main(String args[]){
	
		//Object creation and input passing
		Exp num = new BinaryExp(new NumericConstant(2),OPERATOR.PLUS,new BinaryExp(new NumericConstant(3),OPERATOR.MUL,new NumericConstant(4)));
		System.out.println(num.Evaluate(null));
		
		Exp num1 = new UnaryExp(new NumericConstant(6),OPERATOR.MINUS);
		System.out.println(num1.Evaluate(null));
	}
}
