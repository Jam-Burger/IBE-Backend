// Endpoint configurations with method, body, and query parameters
export const ENDPOINT_CONFIGS = {
  // Health endpoints
  HEALTH: {
    path: '/health',
    method: 'GET',
    body: null,
    queryParams: null,
  },

  // Authentication endpoints
  AUTH: {
    LOGIN: {
      path: '/api/v1/1/auth/login',
      method: 'POST',
      body: {
        email: 'themirza001@gmail.com',
        password: 'password',
      },
      queryParams: null,
    },
  },
    //Config endpoints
   CONFIG:{
        GLOBAL:{
            path:"/api/v1/1/config/GLOBAL",
            method:"GET",
            body:{
                     brand: {
                         logoUrl: "https://d2j0b5x6p8vkw1.cloudfront.net/1/logos/9f2b5ff4-1d09-47b2-b8a3-3f5256d68517",
                         companyName: "Kickdrum Technology Group LLC",
                         pageTitle: "Kickdrum Booking Engine",
                         footerLogoUrl: "https://d2j0b5x6p8vkw1.cloudfront.net/1/logos/footer-image.png"
                     },
                     languages: [
                         {
                             code: "en",
                             name: "English"
                         },
                         {
                             code: "fr",
                             name: "French"
                         },
                         {
                             code: "es",
                             name: "Spanish"
                         }
                     ],
                     currencies: [
                         {
                             code: "USD",
                             symbol: "$"
                         },
                         {
                             code: "EUR",
                             symbol: "€"
                         },
                         {
                             code: "INR",
                             symbol: "₹"
                         }
                     ],
                     properties: [
                         9
                     ]
                 },
            queryParams: null,
        },
        LANDING:{
        path:"/api/v1/1/config/LANDING",
        method:"GET",
        body:{
                banner: {
                    enabled: true,
                    imageUrl: "https://d2j0b5x6p8vkw1.cloudfront.net/1/landing_banners/4f61e46b-f3ac-4c85-a727-733443399be6.avif"
                },
                searchForm: {
                     lengthOfStay: {
                         min: 1,
                         max: 5
                     },
                    guestOptions: {
                        enabled: true,
                        min: 1,
                        max: 6,
                        categories: [
                            {
                                name: "Adults",
                                enabled: true,
                                min: 1,
                                max: 6,
                                default: 2,
                                label: "Ages 18-59"
                            },
                            {
                                name: "Teens",
                                enabled: true,
                                min: 0,
                                max: 4,
                                default: 0,
                                label: "Ages 13-17"
                            },
                            {
                                name: "Children",
                                enabled: true,
                                min: 0,
                                max: 4,
                                default: 0,
                                label: "Ages 0-12"
                            },
                            {
                                name: "Senior Citizens",
                                enabled: true,
                                min: 0,
                                max: 4,
                                default: 0,
                                label: "Ages 60+"
                            }
                        ]
                    },
                    roomOptions: {
                        enabled: true,
                        min: 1,
                        max: 4,
                        default: 1
                    },
                    accessibility: {
                        enabled: true,
                        label: "I need an Accessible Room"
                    }
                 }
             }

        queryParams: null,
        },
        CHECKOUT:{
            path:"/api/v1/1/config/CHECKOUT",
             method:"GET",
              body:{
                 sections: [
                     {
                         title: "Traveler Inf0",
                         id: "traveler_info",
                         enabled: true,
                         fields: [
                             {
                                 label: "First Name",
                                 name: "travelerFirstName",
                                 type: "text",
                                 required: true,
                                     enabled: true,
                                     pattern: "^[A-Za-z]{2,50}$",
                                     options: null
                                 },
                                 {
                                     label: "Last Name",
                                     name: "travelerLastName",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[A-Za-z]{2,50}$",
                                     options: null
                                 },
                                 {
                                     label: "Phone",
                                     name: "travelerPhone",
                                     type: "tel",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{10,15}$",
                                     options: null
                                 },
                                 {
                                     label: "Email",
                                     name: "travelerEmail",
                                     type: "email",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 }
                             ]
                         },
                         {
                             title: "Billing Info",
                             id: "billing_info",
                             enabled: true,
                             fields: [
                                 {
                                     label: "First Name",
                                     name: "billingFirstName",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[A-Za-z]{2,50}$",
                                     options: null
                                 },
                                 {
                                     label: "Last Name",
                                     name: "billingLastName",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[A-Za-z]{2,50}$",
                                     options: null
                                 },
                                 {
                                     label: "Mailing Address 1",
                                     name: "billingAddress1",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 },
                                 {
                                     label: "Mailing Address 2",
                                     name: "billingAddress2",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 },
                                 {
                                     label: "Country",
                                     name: "billingCountry",
                                     type: "select",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[A-Za-z\\s]{2,50}$",
                                     options: [
                                         "USA",
                                         "Canada",
                                         "UK",
                                         "Australia"
                                     ]
                                 },
                                 {
                                     label: "City",
                                     name: "billingCity",
                                     type: "select",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 },
                                 {
                                     label: "State",
                                     name: "billingState",
                                     type: "select",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: [
                                         "AL",
                                         "AK",
                                         "AZ",
                                         "CA",
                                         "NY"
                                     ]
                                 },
                                 {
                                     label: "Zip",
                                     name: "billingZip",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{5}$",
                                     options: null
                                 },
                                 {
                                     label: "Phone",
                                     name: "billingPhone",
                                     type: "tel",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{10,15}$",
                                     options: null
                                 },
                                 {
                                     label: "Email",
                                     name: "billingEmail",
                                     type: "email",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 }
                             ]
                         },
                         {
                             title: "Payment Info",
                             id: "payment_info",
                             enabled: true,
                             fields: [
                                 {
                                     label: "Card Number",
                                     name: "cardNumber",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{16,16}$",
                                     options: null
                                 },
                                 {
                                     label: "Exp MM",
                                     name: "expMonth",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^(0[1-9]|1[0-2])$",
                                     options: null
                                 },
                                 {
                                     label: "Exp YY",
                                     name: "expYear",
                                     type: "text",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{2}$",
                                     options: null
                                 },
                                 {
                                     label: "CVV Code",
                                     name: "cvv",
                                     type: "password",
                                     required: true,
                                     enabled: true,
                                     pattern: "^[0-9]{3,4}$",
                                     options: null
                                 },
                                 {
                                     label: "Send me special offers",
                                     name: "receiveOffers",
                                     type: "checkbox",
                                     required: false,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 },
                                 {
                                     label: "I agree to the Terms and Policies of travel",
                                     name: "agreedToTerms",
                                     type: "checkbox",
                                     required: true,
                                     enabled: true,
                                     pattern: null,
                                     options: null
                                 }
                             ]
                         }
                     ]
                         }
            ,
                    queryParams: null,
        },
        ROOMLIST:{
        path:"/api/v1/1/config/ROOMS_LIST",
        method:"GET",
        body:{
                 banner: {
                     enabled: true,
                     imageUrl: "https://d2j0b5x6p8vkw1.cloudfront.net/1/rooms_page_banners/a90baf0d-b5c5-45ad-b1d3-646f749c98c9"
                 },
                 steps: {
                     enabled: true,
                     labels: [
                         "Choose room",
                         "Choose add on",
                         "Checkout"
                     ]
                 },
                 filters: {
                     sortOptions: {
                         enabled: true,
                         defaultSort: "PRICE_LOW_TO_HIGH",
                         options: [
                             {
                                 value: "PRICE_LOW_TO_HIGH",
                                 label: "Price: Low to High",
                                 enabled: true
                             },
                             {
                                 value: "PRICE_HIGH_TO_LOW",
                                 label: "Price: High to Low",
                                 enabled: true
                             },
                             {
                                 value: "RATING_HIGH_TO_LOW",
                                 label: "Highest Rated",
                                 enabled: true
                             },
                             {
                                 value: "CAPACITY_HIGH_TO_LOW",
                                 label: "Capacity",
                                 enabled: true
                             },
                             {
                                 value: "ROOM_SIZE_LARGE_TO_SMALL",
                                 label: "Room Size",
                                 enabled: true
                             }
                         ]
                     },
                     filterGroups: {
                         ratings: {
                             enabled: true,
                             label: "Guest Ratings",
                             options: [
                                 {
                                     value: 5,
                                     label: "Excellent (5)",
                                     enabled: true
                                 },
                                 {
                                     value: 4,
                                     label: "Very Good (4+)",
                                     enabled: true
                                 },
                                 {
                                     value: 3,
                                     label: "Good (3+)",
                                     enabled: true
                                 }
                             ]
                         },
                         bedTypes: {
                             enabled: true,
                             label: "Bed Types"
                         },
                         bedCount: {
                             enabled: true,
                             label: "Number of Beds",
                             min: 1,
                             max: 4,
                             default: 1
                         },
                         roomSize: {
                             enabled: true,
                             label: "Room Size (sqft)",
                             min: 1,
                             max: 750
                         },
                         amenities: {
                             enabled: true,
                             label: "Room Amenities"
                         }
                     }
                 }
             }
        queryParams: null,
        },
   },

  // Property endpoints
  PROPERTY: {
    DETAIL: {
      path: '/api/v1/properties/9',
      method: 'GET',
      body: null,
      queryParams: null,
    },
  },

  //Room endpoints
  ROOM:{
    RATES:{
    path:'/api/v1/1/9/room-rates/daily-minimum',
    method:'GET',
    body:null,
    queryParams:{
      start_date:'2024-05-01',
      end_date:'2024-06-30',
      }
    },
    AMENTIES:{
    path:'/api/v1/1/9/amenities',
    method:'GET',
    body:null,
    queryParams:null
    }

  },

  //Offer endpoints
  OFFERS:{
    SPECIAL_DISCOUNT:{
        path:"/api/v1/1/9/SPECIAL_DISCOUNT",
        method:"GET",
        body:null,
        queryParams:{
            start_date:'2024-05-01',
            end_date:'2024-06-30',
        }
    },
    CALENDAR_OFFERS:{
    path:"/api/v1/1/9/special-discounts/calender-offers",
     method:"GET",
    body:null,
    queryParams:{
        start_date:'2024-05-01',
        end_date:'2024-06-30',
    }
    },
    PROMO_OFFERS:{
    path:"/api/v1/1/9/special-discounts/promo-offer",
    method:"GET",
    body:null,
    queryParams:{
        start_date:'2024-05-01',
        end_date:'2024-06-30',
        promo_code:'MILITARY20'
    }
    },
  },

//image endpoints
  IMAGES:{
  CREATE:{
  path:'/api/v1/2/images/LANDING_BANNER',
  method:"POST",
  body:null,
    queryParams:null
  }

  },

  // Room Type endpoints
  ROOM_TYPE: {
    LIST: {
      path: '/api/v1/1/9/room-types/filter',
      method: 'GET',
      body: null,
      queryParams: {
        propertyId: 9,
          roomCount: 1,
          totalGuests: 2,
          guest_Adults: 2,
          sortBy: "CAPACITY_HIGH_TO_LOW",
          dateFrom: "2025-03-05",
          dateTo: "2025-03-10",
          roomSizeMin: 1,
          roomSizeMax: 750,
          pageSize: 3,
          page: 1
      },
    },
    DETAIL: {
      path: '/api/v1/room-types/{id}',
      method: 'GET',
      body: null,
      queryParams: {
      start_date: '2025-05-01',
      end_date: '2025-06-30',
      },
    },
  },

  //OTP
  OTP:{
    LIST:{
    path:'api/v1/1/otp/send',
    method:'POST',
    body:null,
    queryParams:{
        email:'themirza001@gmail.com'

    }
    }
  },


  // Booking endpoints
  BOOKING: {
    CREATE: {
      path: '/api/v1/bookings',
      method: 'POST',
      body: {
              formData: {
                travelerFirstName: "saasdad",
                travelerLastName: "dasdas",
                travelerPhone: "1313123221",
                travelerEmail: "jay40793@gmail.com",
                billingFirstName: "dasdadsa",
                billingLastName: "sdfs",
                billingAddress1: "asdasd",
                billingAddress2: "dasad",
                billingCountry: "India",
                billingState: "Panjshir",
                billingCity: "Bāzārak",
                billingZip: "23223",
                billingPhone: "2324432423",
                billingEmail: "abc@gmail.com",
                agreedToTerms: "true",
                receiveOffers: "true",
                expYear: "29",
                cardNumber: "2222222222222222",
                expMonth: "02",
                cvv: "122"
              },
              dateRange: {
                from: "2025-04-13",
                to: "2025-04-20"
              },
              roomCount: 1,
              guests: {
                Adults: 2
              },
              totalAmount: 100,
              roomTypeId: 53,
              bedCount: 10,
              promotionId: null
            },
      queryParams: {
        accessToken:"eyJraWQiOiJSelwvUUJqM0wza1VNU2ZNUWJOK1ZlQWgrZm5taXZpKzRPXC9SNEsraERkZjA9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoicXhuS2NMRlF4dS1QVnFQaHRFbUc2dyIsInN1YiI6ImY0Yjg3ZDBjLTAwYTEtNzBhZC0xMDA2LTQ4NTFhNjA0ZDU3YiIsImNvZ25pdG86Z3JvdXBzIjpbImFwLW5vcnRoZWFzdC0yX2NPQVlvN3A4WF9Hb29nbGUiXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuYXAtbm9ydGhlYXN0LTIuYW1hem9uYXdzLmNvbVwvYXAtbm9ydGhlYXN0LTJfY09BWW83cDhYIiwiY29nbml0bzp1c2VybmFtZSI6Ikdvb2dsZV8xMDM1NzI4MzU5MDYxMjMwMzI2NzEiLCJub25jZSI6IkxxLWRhQlh3VGFTQUhoWW80UmwwbEVMVUZldjEzU2pJUnRqamc5a09VRXR1SGFqRTlUN3dqMnViR08tTXZRUzZQX2tlOXU4MzZqY0w1ekQyck9rYVlWRU9BTm41Ujc3S2ZZMVRJZ2hwSF9QZFVmZUxxd29VRURiS2wwWmdkZ0lRY2JyNkZ0eXRmei1hWlQtTFoyd2tDTEFuZjM2WktGMGpKLTByQUN1SHFySSIsIm9yaWdpbl9qdGkiOiI3YzJkOTlkYS1hYTcxLTRhMTktYTFmNC0zOWE0ODBkNzAxZWUiLCJhdWQiOiI1N3ZnbmlmazR1dnE0YTZwZW0wNTllMDR2bCIsImlkZW50aXRpZXMiOlt7ImRhdGVDcmVhdGVkIjoiMTc0MzU5Mjk2NjU2MyIsInVzZXJJZCI6IjEwMzU3MjgzNTkwNjEyMzAzMjY3MSIsInByb3ZpZGVyTmFtZSI6Ikdvb2dsZSIsInByb3ZpZGVyVHlwZSI6Ikdvb2dsZSIsImlzc3VlciI6bnVsbCwicHJpbWFyeSI6InRydWUifV0sInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNzQ0NjQwODMwLCJleHAiOjE3NDQ2NDQ0MzAsImlhdCI6MTc0NDY0MDgzMCwianRpIjoiYTFmZjZiY2QtNmM4MS00OWM3LThkZjctZWZkMDI3MjdlOGJiIiwiZW1haWwiOiJqYXk0MDc5M0BnbWFpbC5jb20ifQ.WVpGJJBbRuJP9J3AmSiDOs21dIPEYcIXo4zeYp0OZvLn-7fdL_MnESH-gBow5zCdUP5m3QYyR9jS9CJbY6llCN2LQVYty84uzSOLR8kg5NoDDMxIEogOO8IzmixbBfdPZwM0lFDq_KxKbi4X18Kff5OwpWhvhsizA8xuAoAPE-Gh-bKeko3TkplN_XCuyehwOiqEXhYjH7loopy73sdKxKkv8hv-aXtaE6mL8Q0e84-f7g9Oh6c_OACCKPAMYDSMPC8O-XgYF9pAwl7hrMicA6r4FRX2IkGbHQHRf9MM_n4FfyxN2nd9whGQVTktat_9ly78Tf052eCUCeVfPJyINw",
        otp:"302914"
      },
    },
    LIST: {
      path: '/api/v1/1/bookings/139',
      method: 'GET',
      body:null,
      queryParams: null,
    },
    UPDATE:{
    path:'/api/v1/1/bookings/145/cancel',
    method:'PUT',
    body:null,
    queryParams:{
    accessToken:"eyJraWQiOiJSelwvUUJqM0wza1VNU2ZNUWJOK1ZlQWgrZm5taXZpKzRPXC9SNEsraERkZjA9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoicXhuS2NMRlF4dS1QVnFQaHRFbUc2dyIsInN1YiI6ImY0Yjg3ZDBjLTAwYTEtNzBhZC0xMDA2LTQ4NTFhNjA0ZDU3YiIsImNvZ25pdG86Z3JvdXBzIjpbImFwLW5vcnRoZWFzdC0yX2NPQVlvN3A4WF9Hb29nbGUiXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuYXAtbm9ydGhlYXN0LTIuYW1hem9uYXdzLmNvbVwvYXAtbm9ydGhlYXN0LTJfY09BWW83cDhYIiwiY29nbml0bzp1c2VybmFtZSI6Ikdvb2dsZV8xMDM1NzI4MzU5MDYxMjMwMzI2NzEiLCJub25jZSI6IkxxLWRhQlh3VGFTQUhoWW80UmwwbEVMVUZldjEzU2pJUnRqamc5a09VRXR1SGFqRTlUN3dqMnViR08tTXZRUzZQX2tlOXU4MzZqY0w1ekQyck9rYVlWRU9BTm41Ujc3S2ZZMVRJZ2hwSF9QZFVmZUxxd29VRURiS2wwWmdkZ0lRY2JyNkZ0eXRmei1hWlQtTFoyd2tDTEFuZjM2WktGMGpKLTByQUN1SHFySSIsIm9yaWdpbl9qdGkiOiI3YzJkOTlkYS1hYTcxLTRhMTktYTFmNC0zOWE0ODBkNzAxZWUiLCJhdWQiOiI1N3ZnbmlmazR1dnE0YTZwZW0wNTllMDR2bCIsImlkZW50aXRpZXMiOlt7ImRhdGVDcmVhdGVkIjoiMTc0MzU5Mjk2NjU2MyIsInVzZXJJZCI6IjEwMzU3MjgzNTkwNjEyMzAzMjY3MSIsInByb3ZpZGVyTmFtZSI6Ikdvb2dsZSIsInByb3ZpZGVyVHlwZSI6Ikdvb2dsZSIsImlzc3VlciI6bnVsbCwicHJpbWFyeSI6InRydWUifV0sInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNzQ0NjQwODMwLCJleHAiOjE3NDQ2NDQ0MzAsImlhdCI6MTc0NDY0MDgzMCwianRpIjoiYTFmZjZiY2QtNmM4MS00OWM3LThkZjctZWZkMDI3MjdlOGJiIiwiZW1haWwiOiJqYXk0MDc5M0BnbWFpbC5jb20ifQ.WVpGJJBbRuJP9J3AmSiDOs21dIPEYcIXo4zeYp0OZvLn-7fdL_MnESH-gBow5zCdUP5m3QYyR9jS9CJbY6llCN2LQVYty84uzSOLR8kg5NoDDMxIEogOO8IzmixbBfdPZwM0lFDq_KxKbi4X18Kff5OwpWhvhsizA8xuAoAPE-Gh-bKeko3TkplN_XCuyehwOiqEXhYjH7loopy73sdKxKkv8hv-aXtaE6mL8Q0e84-f7g9Oh6c_OACCKPAMYDSMPC8O-XgYF9pAwl7hrMicA6r4FRX2IkGbHQHRf9MM_n4FfyxN2nd9whGQVTktat_9ly78Tf052eCUCeVfPJyINw",
     otp:"302914"
    }
    }
  },

  STAFF:{
    LIST:{
    path:'/api/v1/1/staff-availability',
    method:'GET',
    body:null,
    queryParams:null,
  }
};

// Test endpoint groups
export const TEST_ENDPOINTS = {
  SMOKE: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST,
  ],
  MAIN: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST,
    // ENDPOINT_CONFIGS.BOOKING.LIST,
    // ENDPOINT_CONFIGS.SPECIAL_OFFER.LIST,
  ],
  STRESS: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.AUTH.LOGIN,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST
  ],
}; 