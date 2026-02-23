import { Injectable, signal, computed, inject, effect } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderService } from '../service/order.service';
import { CartItem } from '../models/cart-item.model';
import { Product } from '../models/product';
import { CartCalculator } from '../utils/CartCalculator';
import { OfferState } from './offer.state';
import { Customer } from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class CartState {
  private orderService = inject(OrderService);
  private offerState = inject(OfferState);

  private cartItems = signal<CartItem[]>(this.loadCartFromStorage());

  private weeklyOffers = this.offerState.offers;
  public readonly items = this.cartItems.asReadonly();
  public readonly count = computed(() => this.cartItems().reduce((s, i) => s + i.quantity, 0));
  public readonly totalPrice = computed(() => {
    return this.cartItems().reduce((total, item) => {
      return total + CartCalculator.calculateItemSubtotal(item, this.weeklyOffers());
    }, 0);
  });

  constructor() {
    effect(() => {
      this.saveCartToStorage(this.cartItems());
    });
  }

  addToCart(product: Product, quantity: number) {
    this.cartItems.update(items => {
      const index = items.findIndex(i => String(i.id) === String(product.id));
      if (index > -1) {
        const updated = [...items];
        const newQty = updated[index].quantity + quantity;
        updated[index] = { ...updated[index], quantity: Math.min(newQty, 99) };
        return updated;
      }
      return [...items, { ...product, quantity }];
    });
  }

  updateQuantity(id: string, delta: number) {
    this.cartItems.update(items =>
      items.map(i => {
        if (i.id === id) {
          let newQty = i.quantity + delta;
          if (newQty < 1) newQty = 1;
          if (newQty > 99) newQty = 99;
          return { ...i, quantity: newQty };
        }
        return i;
      })
    );
  }

  removeFromCart(id: string) {
    this.cartItems.update(items => items.filter(i => i.id !== id));
  }

  clearCart() {
    this.cartItems.set([]);
  }

  checkout(customer: Customer): Observable<any> {
    return this.orderService.placeOrder(this.cartItems(), customer);
  }

  private loadCartFromStorage(): CartItem[] {
    const stored = localStorage.getItem('cart_items');
    return stored ? JSON.parse(stored) : [];
  }

  private saveCartToStorage(items: CartItem[]) {
    localStorage.setItem('cart_items', JSON.stringify(items));
  }
}
