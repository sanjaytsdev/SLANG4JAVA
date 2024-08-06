import java.io.*;
import java.lang.*;
import java.util.*;

//Expression visitor for all expressions
interface IExprVisitor{
	double visit(NumericConstant num);
	double visit(BinaryExp bin);
	double visit(UnaryExp un);
}

//Tree visitor to evaluate the code
class TreeEvaluatorVisitor implements IExprVisitor{
	public double visit(NumericConstant num){
		return num.getNumber();
	}
	
	public double visit(BinaryExp bin){
		OPERATOR temp = bin.getOp();
		double lval = bin.getLeft().accept(this);
		double rval = bin.getRight().accept(this);
		return (temp == OPERATOR.PLUS) ? lval + rval : (temp == OPERATOR.MUL) ? lval * rval : (temp == OPERATOR.DIV) ? lval / rval : lval - rval;
	}
	
	public double visit(UnaryExp un){
		OPERATOR temp = un.getOp();
		double rval = un.getRight().accept(this);
		return (temp == OPERATOR.PLUS) ?  +rval : -rval;
	}
}

//Prints expressions followed by the operator ie. postfix notation
class ReversePolishEvaluator implements IExprVisitor{
	public double visit(NumericConstant num){
		System.out.print(num.getNumber()+" ");
		return 42;
	}
	
	public double visit(BinaryExp bin){
		OPERATOR temp = bin.getOp();
		bin.getLeft().accept(this);
		bin.getRight().accept(this);
		System.out.print(((temp == OPERATOR.PLUS) ? "+": (temp == OPERATOR.MUL) ? "*" : (temp == OPERATOR.DIV) ? "/" : "-"));
		return 42;
	}
	
	public double visit(UnaryExp un){
		OPERATOR temp = un.getOp();
		un.getRight().accept(this);
		System.out.print(((temp == OPERATOR.PLUS) ? "+": "-"));
		return 42;
	}
}

//Stack visitor to evaluate the code
class StackEvaluatorVisitor implements IExprVisitor{
	Stack<Double> stk = new Stack<>(); 
	
	public double getNumber(){
		return stk.pop();
	} 
	
	public double visit(NumericConstant num){
		stk.push(num.getNumber());
		return -1;
	}
	
	public double visit(BinaryExp bin){
		OPERATOR temp = bin.getOp();
		double lval = bin.getLeft().accept(this);
		double rval = bin.getRight().accept(this);
		lval = stk.pop();
		rval = stk.pop();
		
		if(temp == OPERATOR.PLUS)
			stk.push(lval + rval);
		else if(temp == OPERATOR.MUL)
			stk.push(lval * rval);
		else if(temp == OPERATOR.DIV)
			stk.push(lval / rval);
		else
			stk.push(lval - rval);
		return -1;
	}
	
	public double visit(UnaryExp un){
		OPERATOR temp = un.getOp();
		double rval = un.getRight().accept(this);
		rval = stk.pop();
		
		if(temp == OPERATOR.PLUS)
			stk.push(+rval);
		else 
			stk.push(-rval);
		return -1;
	}
}

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
	public abstract double accept(IExprVisitor exv);
}

//Class for holding the value in an expression --> if 2 + 3 is an expression 2 & 3 are numeric constants
class NumericConstant extends Exp{
	private double value;
	
	public NumericConstant(double value){
		this.value = value;
	}
	
	@Override
	public double accept(IExprVisitor exv){
		return exv.visit(this);
	}
	
	public double getNumber(){
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

	
	public OPERATOR getOp(){
		return op;
	}
	
	public Exp getLeft(){
		return ex1;
	}
	
	public Exp getRight(){
		return ex2;
	}
	
	@Override
	public double accept(IExprVisitor exv){
		return exv.visit(this);
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
	
	public OPERATOR getOp(){
		return op;
	}
	
	public Exp getLeft(){
		return null;
	}
	
	public Exp getRight(){
		return ex1;
	}
	
	@Override
	public double accept(IExprVisitor exv){
		return exv.visit(this);
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
public class Program1 {
	public static void main(String args[]){
		if(args.length != 1)
			return;
		ExpressionBuilder b = new ExpressionBuilder(args[0]);
		Exp e = b.GetExpression();
		double x = e.accept(new TreeEvaluatorVisitor());
		System.out.println("Tree Evaluation Result = " + x);
		System.out.print("Postfix Notation = ");
		e.accept(new ReversePolishEvaluator());
		System.out.println();
		StackEvaluatorVisitor m = new StackEvaluatorVisitor();
		e.accept(m);
		System.out.println("Stack Evaluation Result: " + m.getNumber());

	}
}
