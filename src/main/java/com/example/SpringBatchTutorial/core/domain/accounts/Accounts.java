package com.example.SpringBatchTutorial.core.domain.accounts;

import java.util.Date;

import com.example.SpringBatchTutorial.core.domain.orders.Orders;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@Entity
public class Accounts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String orderItem;
	private Integer price;
	private Date orderDate;
	private Date accountDate;

	public Accounts(Orders orders) {
		this.id = orders.getId();
		this.orderItem = orders.getOrderItem();
		this.price = orders.getPrice();
		this.orderDate = orders.getOrderDate();
		this.accountDate = new Date();
	}
}
