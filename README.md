# Stealthy Link Shortener

Stealthy Link Shortener is a URL shortener that redirects to different destination URLs depending on the user's location. 

## About this project

Sharing truthful information in Russia has become complicated recently, especially after the war started.
Almost all independent social media and many popular social networks (Instagram, Facebook, Twitter) are blocked.
Some were designated extremist organizations, like the Anti-Corruption Foundation by Alexei Navalny and Meta Platforms. 

Fortunately, due to these tight restrictions, many Russians started using VPN services to access all the blocked information.

Unfortunately, there is still a risk of legal prosecution even for posting links on social media because
it may be considered extremism, dissemination of "unreliable information", "discrediting" the Russian military,
"LGBT propaganda" (which is also forbidden), etc.

This project aims to make spreading information safer for those who stay in Russia. Technically, a link posted with
this service cannot be considered disseminating *anything* among Russians because those with a Russian IP address
don't even see the resource's name. But this service also doesn't affect the audience reach significantly because
most of the unsafe resources are already blocked and cannot be accessed from Russia without VPN anyway.

> **Warning**
> I cannot give any guarantee that this will help. No one can protect anyone in Russia.

## Website

Try this URL shortener here: [isolink.site](https://isolink.site)

## Technologies

- Java 17
- Spring Boot
- MongoDB + Spring Data
- Thymeleaf + Spring MVC
- Docker

## Idea

The idea belongs to the Russian meme cabbage guy [@A_Kapustin](https://twitter.com/A_Kapustin).  
[The twit](https://twitter.com/A_Kapustin/status/1531958391823863809):

<img src="https://raw.githubusercontent.com/wiki/isomethane/stealthy-link-shortener/images/A_Kapustin_twit.png" alt="The twit with the idea of this project by @A_Kapustin" width="50%"><img src="https://raw.githubusercontent.com/wiki/isomethane/stealthy-link-shortener/images/A_Kapustin_twit_en.png" alt="The twit with the idea of this project by @A_Kapustin translated into English" width="50%">
