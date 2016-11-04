(ns chatter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.page :as page]
            [hiccup.form :as form]))

(def chat-messages (atom '()))

(defn update-messages!
  "This will update a message list atom"
  [messages name new-message]
  (swap! messages conj {:name name :message new-message}))

(defn generate-message-view
  "Generates the HTML for displaying messages"
  [messages]
  (page/html5
    [:head
      [:title "chatter"]
      (page/include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
      (page/include-js  "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
      (page/include-css "/chatter.css")]
    [:body
      [:h1 "Shayne's Chat App"]
      [:p
        (form/form-to
          [:post "/"]
          "Name: " (form/text-field "name")
          "Message: " (form/text-field "msg")
          (form/submit-button "Submit"))]
      [:p
        [:table#messages.table.table-striped
          (map (fn [m] [:tr [:td (:name m)] [:td (:message m)]]) messages)]]]))

(defroutes app-routes
  (GET "/" [] (generate-message-view @chat-messages))
  (POST "/" {params :params} (generate-message-view
                               (update-messages! chat-messages
                                 (get params "name")
                                 (get params "msg"))))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (wrap-params app-routes))
