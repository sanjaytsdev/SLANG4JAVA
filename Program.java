import java.io.*;
import java.lang.*;

//Enum for the token
enum TOKEN{
	ILLEGAL_TOKEN(-1),
	TOK_PLUS(1),
	TOK_MINUS(2),
	TOK_MUL(3),
	TOK_DIV(4),
	TOK_OPEN(5),
	TOK_CLOSE(6),
	TOK_DOUBLE(7),
	TOK_NULL(8);	
	
	private final int val;
	
	TOKEN(int val){
		this.val = val;
	}
	
	public int getValue(){
		return val;
	}
}

//Lexical analyzer for tokenizing the input
class Lexer{
	String IExpr;
	int index, length;
	double num;
	
	public Lexer(String IExpr){
		this.IExpr = IExpr;
		this.length = IExpr.length();
		index = 0;
	}
	
	public TOKEN getToken(){
		TOKEN t = TOKEN.ILLEGAL_TOKEN;
		
		//Skipping the whitspace
		while(index < length && (IExpr.charAt(index) == ' ' || IExpr.charAt(index) == '\t'))
			index++;
			
		if(index == length)
			return TOKEN.TOK_NULL;
			
		switch(IExpr.charAt(index)){
			
			case '+': t = TOKEN.TOK_PLUS;
				  index++;
				  break;
			case '-': t = TOKEN.TOK_MINUS;
				  index++;
				  break;
			case '*': t = TOKEN.TOK_MUL;
				  index++;
				  break;
			case '/': t = TOKEN.TOK_DIV;
				  index++;
				  break;
			case '(': t = TOKEN.TOK_OPEN;
				  index++;
				  break;
			case ')': t = TOKEN.TOK_CLOSE;
				  index++;
				  break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				{
					String str = "";
					while(index < length && Character.isDigit(IExpr.charAt(index))) {
						str += IExpr.charAt(index); 
						index++;
					}
					
					num = Double.parseDouble(str);
					t = TOKEN.TOK_DOUBLE; 
				}
				break;
			default: System.out.println("Error while analyzing tokens");
		}
		return t;
	}
	
	public double getNumber(){
		return num;
	}

}

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

//Recursive Descent Parser(RDP) for generating Abstract Syntax Tree(AST)
class RDParser extends Lexer{
	TOKEN current_token;
	
	public RDParser(String str){
		super(str);
	}
	
	public Exp CallExpr(){
		current_token = getToken();
		return Expr();
	}
	
	// <Expr> := <Term> | <Term> { + | - } <Expr>
	public Exp Expr(){
		TOKEN l_token;
		Exp retValue = Term();
		
		while(current_token == TOKEN.TOK_PLUS || current_token == TOKEN.TOK_MINUS){
			l_token = current_token;
			current_token = getToken();
			Exp e1 = Expr();
			retValue = new BinaryExp(retValue, l_token == TOKEN.TOK_PLUS ? OPERATOR.PLUS : OPERATOR.MINUS,e1);
		}	
		return retValue;
	}

	// <Term> := <Factor> | <Factor> { * | / } <Term>
	public Exp Term(){
		TOKEN l_token;
		Exp retValue = Factor();
		
		while(current_token == TOKEN.TOK_MUL || current_token == TOKEN.TOK_DIV){
			l_token = current_token;
			current_token = getToken();
			Exp e1 = Term();
			retValue = new BinaryExp(retValue, l_token == TOKEN.TOK_MUL ? OPERATOR.MUL : OPERATOR.DIV, e1);
		}	
		return retValue;
	}

	// <Factor> := <Number> | '(' <Term> ')' | { + | - } <Factor>
	public Exp Factor(){
		TOKEN l_token;
		Exp retValue = null;
		
		if(current_token == TOKEN.TOK_DOUBLE){
			retValue = new NumericConstant(getNumber());
			current_token = getToken();
		} else if(current_token == TOKEN.TOK_OPEN){
			current_token = getToken();
			retValue = Expr();
			
			if(current_token != TOKEN.TOK_CLOSE){
				System.out.println("Missing closing parenthesis");
			}
			
		} else if(current_token == TOKEN.TOK_PLUS || current_token == TOKEN.TOK_MINUS){
			l_token = current_token;
			current_token = getToken();
			retValue = Factor();
			retValue = new UnaryExp(retValue, l_token == TOKEN.TOK_PLUS ? OPERATOR.PLUS : OPERATOR.MINUS);
		} else {
			System.out.println("Illegal token");
		}
		return retValue;
	}
}

//Responsible for building AST
class AbstractBuilder{}
class ExpressionBuilder extends AbstractBuilder{
	String expression;
	
	public ExpressionBuilder(String expression){
		this.expression = expression;
	}
	
	public Exp GetExpression(){
		try{
			RDParser r = new RDParser(expression);
			return r.CallExpr(); 
		} catch(Exception e){
			return null;
		}
	}
}

//Main class & method
public class Program {
	public static void main(String args[]){
		ExpressionBuilder b = new ExpressionBuilder(args[0]);
		Exp e = b.GetExpression();
		System.out.println(e.Evaluate(null));
	}
}
