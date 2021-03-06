package com.bookstore.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.dao.AddressDAO;
import com.bookstore.dao.AppriseDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.dao.BuyItemDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.PictureDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.domain.Address;
import com.bookstore.domain.Apprise;
import com.bookstore.domain.Book;
import com.bookstore.domain.BookType;
import com.bookstore.domain.BuyItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.Picture;
import com.bookstore.domain.User;
import com.bookstore.service.ConvertorService;
import com.bookstore.util.DivBook;
import com.bookstore.util.DivOrder;
import com.bookstore.util.LiApprise;
import com.bookstore.util.TrCartItem;
import com.bookstore.util.TypeAndBookListItem;

/**
 * @author Zhiqi Yang
 * @author Chang Su
 * @description 一些常用的jsp与action之间的数据结构转换函数
 * @modify 
 * @modifyDate May 10
 */
public class ConvertorServiceImpl implements ConvertorService{
	
	//IOC Dao
	BookDAO bookDAO;
	PictureDAO pictureDAO;
	BuyItemDAO buyItemDAO;
	AddressDAO addressDAO;
	OrderDAO orderDAO;
	AppriseDAO appriseDAO;
	UserDAO userDAO;
	
	public Double calculateTotalPrice(List<TrCartItem> trCartItemList){
		Double fTotalPrice = 0.0;
		for(TrCartItem trCartItem : trCartItemList){
			fTotalPrice += Double.parseDouble(trCartItem.getBuyItemPrice());
		}
		return fTotalPrice;
	}
	
	
	public List<TrCartItem> buyItemListToTrCartList(List<BuyItem> buyItemList){
		List<TrCartItem> trCartItemList = new ArrayList<TrCartItem>();
		for(BuyItem buyItem : buyItemList){
			trCartItemList.add(buyItemToTrCartItem(buyItem));
		}
		return trCartItemList;
	}
	
	@Override
	public List<TrCartItem> buyItemIDListToTrCartList(List<Integer> buyItemIDList) {
		List<TrCartItem> trCartItemList = new ArrayList<TrCartItem>();
		for(Integer buyItemID : buyItemIDList){
			trCartItemList.add(buyItemIDToTrCartItem(buyItemID));
		}
		return trCartItemList;
	}
	

	
	@Override
	public TrCartItem buyItemToTrCartItem(BuyItem buyItem) {
		TrCartItem trCartItem = new TrCartItem();
		Integer bookID = buyItem.getBookID();
		Book book = bookDAO.findByID(bookID);
		trCartItem.setBookName(book.getBookName());
		trCartItem.setBookID(book.getBookID());
		trCartItem.setBuyItemID(buyItem.getBuyItemID());
		trCartItem.setAuthorName(book.getAuthor());
		List<Picture> pictureList = pictureDAO.findByBookID(book.getBookID());
		String pictureUrl;
		if(pictureList.isEmpty()) 
			pictureUrl = "not have pictrue"; // picture?
		else
			pictureUrl = pictureList.get(0).getUrl();
		trCartItem.setImageUrl(pictureUrl);
		trCartItem.setNum(buyItem.getBuyNum());
		DecimalFormat df = new DecimalFormat("0.00");
		trCartItem.setPrice(df.format(buyItem.getCurrentPrice()));
		trCartItem.setBuyItemPrice(df.format(Double.parseDouble(trCartItem.getPrice()) * trCartItem.getNum()));
		return trCartItem;
	}

	@Override
	public TrCartItem buyItemIDToTrCartItem(Integer buyItemID) {
		BuyItem buyItem = buyItemDAO.findByID(buyItemID);
		return buyItemToTrCartItem(buyItem);
	}
	
	
	public DivOrder orderIDToDivOrder(Integer orderID){
		return buyItemListAddToDivOrder(getBuyItemList(orderID), orderID);
	}
	
	
	
	// 以下是和Order显示有关
	@Override
	public List<BuyItem> getBuyItemList(Integer orderID) {
		List<BuyItem> buyItemList = buyItemDAO.findByOrderID(orderID);
		return buyItemList;
	}
	/**
	 * @author Yu Fu
	 * @description 由book domain转换为divbook
	 * @modify 
	 * @modifyDate May 10
	 */
	@Override
	public List<DivBook> bookIDToDivBook(List<Book> bookList) {
		// TODO Auto-generated method stub
		List<DivBook> divBookList = new ArrayList<DivBook>();
		
		int i = 0;
		
		for(; i < bookList.size();i++){
			Book book = bookDAO.findByID(bookList.get(i).getBookID());
			Picture picture = pictureDAO.findByBookID(book.getBookID()).get(0);
			DivBook divBook = new DivBook();
			
			divBook.setBookID(book.getBookID());
			divBook.setBookName(book.getBookName());
			divBook.setBookPrice(book.getPrice()*book.getDiscount()/100);
			divBook.setDescription(book.getDescription());
			divBook.setAuthor(book.getAuthor());
			
			
			divBook.setPictureID(picture.getPictureID());
			divBook.setURL(picture.getUrl());
			//System.out.println(divBook.getBookID()+" "+divBook.getPictureID());
			
			divBookList.add(divBook);
			//System.out.println(divBookList.get(i).getBookID()+" "+ divBookList.get(i).getPictureID());
		}
		
		for(int j = 0; j<divBookList.size();j++){
			System.out.println(divBookList.get(j).getBookID()+" "+ divBookList.get(j).getPictureID());
		}
		return divBookList;
	}
	

	@Override
	public DivBook bookToDivBook(Book book) {
		Picture picture = pictureDAO.findByBookID(book.getBookID()).get(0);
		
		DivBook divBook = new DivBook();
		divBook.setBookID(book.getBookID());
		divBook.setBookName(book.getBookName());
		divBook.setBookPrice(book.getPrice()*book.getDiscount()/100);
		divBook.setDescription(book.getDescription());
		divBook.setAuthor(book.getAuthor());
		
		
		divBook.setPictureID(picture.getPictureID());
		divBook.setURL(picture.getUrl());
		return null;
	}

	@Override
	public DivOrder buyItemListAddToDivOrder(List<BuyItem> buyItemList, Integer orderId) {
		List<TrCartItem> trCartItemList = buyItemListToTrCartList(buyItemList);
		
		DivOrder divOrder = new DivOrder();
		Order order = orderDAO.findById(orderId);
		Integer addressId = order.getAddressID();
		Address address = addressDAO.findByID(addressId);
		divOrder.setOrderId(orderId);
		divOrder.setAddress(address.getAddress());
		divOrder.setConsignee(address.getPerson());
		divOrder.setDealTime(order.getBuyTime());
		int stats = order.getStats();
		if(stats == 1) {
			divOrder.setOrderStats("已付款");
		}else if(stats == 2) {
			divOrder.setOrderStats("已取消");
		}else if(stats == 0) { 
			divOrder.setOrderStats("未付款");
		}else {
			divOrder.setOrderStats("已删除");
		}
		divOrder.setTel(address.getTel());
		Double fTotalPrice = 0.0;
		for(BuyItem buyItem: buyItemList){
			int num = buyItem.getBuyNum();
			double currentPrice = buyItem.getCurrentPrice();
			fTotalPrice += num * currentPrice;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		divOrder.setTotalPrice(df.format(fTotalPrice));
		divOrder.setOrderItemList(trCartItemList);
		return divOrder;
	}

	@Override
	public DivOrder buyItemIDListAddToDivOrder(List<Integer> buyItemIDList, Integer orderId) {
		List<TrCartItem> trCartItemList = new ArrayList<TrCartItem>();
		Double fTotalPrice = 0.0;
		for(Integer buyItemId : buyItemIDList) {
			BuyItem buyItem = buyItemDAO.findByID(buyItemId);
			int num = buyItem.getBuyNum();
			double currentPrice = buyItem.getCurrentPrice();
			fTotalPrice += num * currentPrice;
			TrCartItem trCartItem = buyItemIDToTrCartItem(buyItemId);
			trCartItemList.add(trCartItem);
		}
		
		DivOrder divOrder = new DivOrder();
		Order order = orderDAO.findById(orderId);
		Integer addressId = order.getAddressID();
		Address address = addressDAO.findByID(addressId);
		divOrder.setOrderId(orderId);
		divOrder.setAddress(address.getAddress());
		divOrder.setConsignee(address.getPerson());
		divOrder.setDealTime(order.getBuyTime());
		int stats = order.getStats();
		if(stats == 1) {
			divOrder.setOrderStats("已付款");
		}else if(stats == 2) {
			divOrder.setOrderStats("已取消");
		}else if(stats == 0) { 
			divOrder.setOrderStats("未付款");
		}
		divOrder.setTel(address.getTel());
		DecimalFormat df = new DecimalFormat("0.00");
		divOrder.setTotalPrice(df.format(fTotalPrice));
		divOrder.setOrderItemList(trCartItemList);
		return divOrder;
	}
	

	public List<TypeAndBookListItem> bookTypeToTypeAndBookListItemList(List<BookType> bookTypeList){
		List<TypeAndBookListItem> resultList = new ArrayList();
		for(BookType bookType : bookTypeList){
			TypeAndBookListItem item = new TypeAndBookListItem();
			item.setBookType(bookType);
			List<Book> bookList = bookDAO.findByTypeID(bookType.getTypeID());
			List<DivBook> divBookList = bookIDToDivBook(bookList);
			item.setBookList(divBookList);
			resultList.add(item);
		}
		return resultList;
	}
	

	@Override
	public List<LiApprise> appriseListToLiApprise(List<Apprise> appriseList) {
		List<LiApprise> liAppriseList = new ArrayList<LiApprise>();
		for(Apprise a: appriseList){
			liAppriseList.add(appriseToLiApprise(a));
		}
		return liAppriseList;
	}


	@Override
	public LiApprise appriseIDToLiApprise(Integer appriseID) {
		Apprise apprise = appriseDAO.findByID(appriseID);
		LiApprise liApprise = appriseToLiApprise(apprise);
		return liApprise;
	}


	@Override
	public LiApprise appriseToLiApprise(Apprise apprise) {
		User user = userDAO.findById(apprise.getUserID());
		LiApprise liApprise = new LiApprise();
		liApprise.setAppriseID(apprise.getAppriseID());
		liApprise.setBookID(apprise.getBookID());
		liApprise.setScore(apprise.getScore());
		liApprise.setText(apprise.getText());
		liApprise.setUserID(user.getUserID());
		liApprise.setUserName(user.getUserName());
		liApprise.setAppriseTime(apprise.getAppriseTime());
		return liApprise;
	}

	
	public BookDAO getBookDAO() {
		return bookDAO;
	}

	public void setBookDAO(BookDAO bookDAO) {
		this.bookDAO = bookDAO;
	}

	public PictureDAO getPictureDAO() {
		return pictureDAO;
	}

	public void setPictureDAO(PictureDAO pictureDAO) {
		this.pictureDAO = pictureDAO;
	}


	public BuyItemDAO getBuyItemDAO() {
		return buyItemDAO;
	}


	public void setBuyItemDAO(BuyItemDAO buyItemDAO) {
		this.buyItemDAO = buyItemDAO;
	}


	public AddressDAO getAddressDAO() {
		return addressDAO;
	}


	public void setAddressDAO(AddressDAO addressDAO) {
		this.addressDAO = addressDAO;
	}


	public OrderDAO getOrderDAO() {
		return orderDAO;
	}


	public void setOrderDAO(OrderDAO orderDAO) {
		this.orderDAO = orderDAO;
	}


	public AppriseDAO getAppriseDAO() {
		return appriseDAO;
	}


	public void setAppriseDAO(AppriseDAO appriseDAO) {
		this.appriseDAO = appriseDAO;
	}


	public UserDAO getUserDAO() {
		return userDAO;
	}


	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}










	
	
}
