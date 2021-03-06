package com.bookstore.service;
import java.util.List;
import com.bookstore.domain.BuyItem;

public interface CartService {
	boolean addCartItem(int userID, int bookID, int num);
	boolean changeNumOfCartItem(int buyItemID, int num);
	List<BuyItem> getCartItemList(int UserID);
	boolean deleteCartItem(int buyItemID);
}
