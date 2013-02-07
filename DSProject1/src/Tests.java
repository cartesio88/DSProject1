
public class Tests {

	public static void main(String[] args){
		/* Test cases - Articles */
		Article a1 = new Article("Sports;Joe;UMN;This is an article");
		System.out.println(a1);
		
		Article a2 = new Article("Sports;Joe;UMN;");
		System.out.println(a2);
		
		Article a3 = new Article(";Joe;;");
		System.out.println(a3);
		
		Article a4 = new Article("NaN;Joe;;");
		System.out.println(a4);
		
		Article a5 = new Article(";;;Article");
		System.out.println(a5);
		
		Article a6 = new Article(";;;");
		System.out.println(a6);
		
		Article a7 = new Article("Distributed Systems");
		System.out.println(a7);
		
		Article a8 = new Article("Science;;;");
		System.out.println(a8);
	}
}
