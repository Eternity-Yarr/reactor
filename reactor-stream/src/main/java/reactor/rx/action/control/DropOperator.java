/*
 * Copyright (c) 2011-2014 Pivotal Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package reactor.rx.action.control;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.Publishers;
import reactor.core.subscriber.SubscriberWithDemand;
import reactor.core.support.BackpressureUtils;

/**
 * @author Stephane Maldini
 * @since 2.0, 2.1
 */
public final class DropOperator<O> implements Publishers.Operator<O, O> {

	public final static DropOperator INSTANCE = new DropOperator();

	@Override
	public Subscriber<? super O> apply(Subscriber<? super O> subscriber) {
		return new DropAction<>(subscriber);
	}

	final static class DropAction<O> extends SubscriberWithDemand<O, O> {

		public DropAction(Subscriber<? super O> actual) {
			super(actual);
		}

		@Override
		protected void doOnSubscribe(Subscription subscription) {
			subscriber.onSubscribe(this);
			requestMore(Long.MAX_VALUE);
		}

		@Override
		protected void doRequested(long before, long n) {
		}

		@Override
		protected void doNext(O o) {
			if(BackpressureUtils.getAndSub(REQUESTED, this, 1L) != 0L) {
				subscriber.onNext(o);
			}
		}

	}
}
