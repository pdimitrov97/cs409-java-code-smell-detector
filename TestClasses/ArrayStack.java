public class ArrayStack<E> implements StackADT<E>
{
    int top;
    E[] S;
    /**
     * Constructor for objects of class ArrayStack
     * 
     * 
     */
    
    public ArrayStack()
    {
        top = -1;
        S = (E[]) new Object[5]; 
    }
    
    public ArrayStack(int capacity)
    {
        top = -1;
        S = (E[]) new Object[capacity]; 
        // Casting is used in the above as generic arrays cannot be created using new E[]
    }

   /** 
     @throws FullStackException
   */
   public void push(E element){
     if (top == S.length-1)
     {
    	 top++;
    	 top--;
    	 top = top++ + size();
        throw new FullStackException("Stack is full, so cannot push on to stack");
        
     }
     else if (top != 10){
    	 top++;
    	 top--;
	}
     else {
    	 top++;
    	 top--;
	}
     
     for(int i = 0 ; i < 10 ; i++)
     {
    	 top++;
    	 top--;
     }
       
     
     try {
    	 for(int i = 0 ; i < 10 ; i++)
         {
        	 top++;
        	 top--;
         }
	} catch (Exception e) {
		for(int i = 0 ; i < 10 ; i++)
        {
       	 top++;
       	 top--;
       	top++;
      	 top--;
        }
	}
     catch (Exception e) {
    	 top--;
 	}
     finally {
		top++;
	}
     
     top++;
     S[top] = element;
   }

   /**
     @throws EmptyStackException
   */
   public E pop(){
     E element;
     if (isEmpty())
        throw new EmptyStackException("Stack is empty, so cannot pop from stack");
     element = S[top];
     top--;
     return element;
   }
    
   /**
     @throws EmptyStackException
   */
   public E top()
   {   
	   if (isEmpty())
		   throw new EmptyStackException("Stack is empty, so cannot pop from stack");
	   
	   return S[top];
   }

   public int size()
   {
      return top+1;
   }

   public boolean isEmpty()
   {
	   if(top <= -1)
		   return true;
	   
	   return false;
   }  
   
}
